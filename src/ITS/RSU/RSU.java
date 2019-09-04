package ITS.RSU;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import ITS.regolatoriSemafori.Regolatore;
import ITS.regolatoriSemafori.RegolatoreASoglia;
import ITS.regolatoriSemafori.RegolatoreClassico;
import network.CityGraph;
import network.NetEdge;
import network.NetGraph;
import network.NetNode;
import network.algorithm.Dijkstra;
import network.message.Message;
import simEventiDiscreti.Entity;
import simEventiDiscreti.Event;
import simEventiDiscreti.Scheduler;
import statistiche.ServerStatistiche;
import statistiche.VariabileStatRSU;
import statistiche.VariabileStatVeicolo;
import util.Param;
import util.Path;
import veicoli.Vehicle;

public class RSU extends Thread implements Entity, RemoteRSU {

	private NetNode nodo;
	private NetGraph grafo;
	private LinkedList<Vehicle> listaAutoIncrocio = new LinkedList<Vehicle>();
	private int ID;
	// tabella di routing con la lista degli archi per arrivare a destinazione
	private HashMap<NetNode, LinkedList<Path>> routingTable = new HashMap<>();
	private ArrayList<NetEdge> archiEntranti, archiUscenti;
	private ServerStatistiche serverStatistiche;
	private VariabileStatRSU variabileRSU;
	private Regolatore regolatore;
	private static final boolean regolatoreClassico = Param.semaforoClassico;
	private static JFrame frame = new JFrame();
	private static JTextArea area = new JTextArea("MESSAGGI SIMULAZIONE");
	private static JButton b = new JButton("Interrupt simulation");
	private static JScrollPane scrollPane = new JScrollPane(area);

	public RSU(Node node, int i) {
		super();
		ID = i;
		nodo = (NetNode) node;
		grafo = nodo.getGraph();
		variabileRSU = new VariabileStatRSU(this, ID);
		initGUI();
	}

	private void initGUI() {
		frame.setLocation(400, 700);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation((dim.width / 2 - frame.getSize().width / 2)+500, dim.height / 2 - frame.getSize().height / 2);
		area.setEditable(false);
		area.setLineWrap(true);
		Font font = new Font(area.getFont().getName(), Font.BOLD, 16);
		area.setFont(font);
		b.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		frame.add(scrollPane, BorderLayout.CENTER);
		frame.add(b, BorderLayout.SOUTH);
		frame.setSize(800, 600);
		frame.setVisible(true);
	}

	@Override
	public void run() {

		init();
		double time = getScheduler().getCurrentTime();
		variabileRSU.updateTempoInizio(time);

		while (true) {
			try {
				serverStatistiche.updateStatisticheRSU(this, variabileRSU);
				Thread.sleep(1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// Se non ci sono più auto in circolazione posso uscire
			if (((CityGraph) grafo).getNodiInMovimento().size() == 0) {
				break;
			}
		}

//		time = getScheduler().getCurrentTime();
//		variabileRSU.updateTempoFine(time);
//		variabileRSU.updateDurataTotale();
		try {
			serverStatistiche.richiestaStatisticheGenerali();
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	// METHODS ////////////////////////////

	@Override
	public void stampaStatistiche(String statistica) throws RemoteException {
		area.append("\n"+this + " ricevute le statistiche generali dal server");
		scrollPane.getViewport().setViewPosition(new Point(0, area.getDocument().getLength()));
	}

	public synchronized void init() {
		archiUscenti = new ArrayList<>(10);
		archiEntranti = new ArrayList<>(10);

		for (Edge edge : grafo.getEachEdge()) {
			// preparo gli archi uscenti
			if (edge.getSourceNode().equals(nodo)) {
				archiUscenti.add((NetEdge) edge);
			}
			// preparo gli archi entranti da assegnare al regolatore dei semafori
			else if (edge.getTargetNode().equals(nodo)) {
				archiEntranti.add((NetEdge) edge);
			}
		}
		// genera regolatore dei semafori
		if (regolatoreClassico) regolatore = new RegolatoreClassico(this, archiEntranti);
		else regolatore = new RegolatoreASoglia(this, archiEntranti);

		regolatore.init();

		routing();

		try {
			UnicastRemoteObject.exportObject(this, 1098);
			serverStatistiche = (ServerStatistiche) Naming.lookup("Server");

			// iscrivo l'RSU alla lista del serverStatistiche
			serverStatistiche.registraRSU(this);
		} catch (MalformedURLException | NotBoundException | RemoteException e) {
			e.printStackTrace();
		}

	}

	public void handler(Event message) {
		if (!(message instanceof Message))
			throw new IllegalArgumentException("the event sended at " + this + " is not a message");

		Message m = (Message) message;
		String nameMessage = m.getName();

		if (nameMessage.equals("ARRIVA MACCHINA")) {
			variabileRSU.updateMessaggiRicevutiRSU_RSU();
			Vehicle v = (Vehicle) m.getData()[0];
			listaAutoIncrocio.add(v);
			variabileRSU.updateVeicoliPerRSU();
		}

		else if (nameMessage.equals("CAMBIO ARCO")) {
			variabileRSU.updateMessaggiRicevutiRSU_Veicolo();
			// indirizza auto
			Vehicle vehicle = (Vehicle) m.getSource();
			NetEdge nextEdge = scegliProssimoArco(vehicle.getTargetNode(), vehicle.getCurrentEdge());

			// se è null vuol dire che la macchina è arrivata a destinazine
			if (nextEdge != null) {
				Message toRsu = new Message("ARRIVA MACCHINA", nodo, nextEdge.getTargetNode(), Param.elaborationTime);
				toRsu.setData(vehicle);
				sendEvent(toRsu);
				variabileRSU.updateMessaggiInviatiRSU_RSU();
				area.append("\n" + this + " -> il veicolo N° " + vehicle.getId()
								+ " deve proseguire per la strada: arco " + nextEdge);
				scrollPane.getViewport().setViewPosition(new Point(0, area.getDocument().getLength()));
			}

			// System.out.println(this+" arrivato cambio arco da "+vehicle);
			// comunica all'auto in quale arco è stata indirizzata
			Message direzione = new Message("DIREZIONE", nodo, vehicle, Param.elaborationTime);
			direzione.setData(nextEdge);
			sendEvent(direzione);
			variabileRSU.updateMessaggiInviatiRSU_Veicolo();
		}

		else if (nameMessage.equals("RICHIESTA PERCORSO")) {
			variabileRSU.updateMessaggiRicevutiRSU_Veicolo();
			Vehicle vehicle = (Vehicle) m.getSource();
			NetNode destinationOfVehicle = (NetNode) m.getData()[0];
			((CityGraph) grafo).setNodoInMovimento(vehicle);

			// System.out.println("\n"+this+": richiesta percorso di "+vehicle+" verso
			// "+destinationOfVehicle);
			NetEdge nextEdge = scegliProssimoArco(destinationOfVehicle, vehicle.getCurrentEdge());

			area.append("\n" + this + " arrivato messaggio RICHIESTA PERCORSO da veicolo N° " + vehicle.getId()
							+ "\n\tcon destinazione: " + destinationOfVehicle + ", prossima strada da raggiungere: arco "
							+ nextEdge);
			scrollPane.getViewport().setViewPosition(new Point(0, area.getDocument().getLength()));

			// System.out.println("------ arco consigliato "+nextEdge);

			// comunica all'auto su quale arco è stata indirizzata
			Message forCar = new Message("DIREZIONE", nodo, vehicle, Param.elaborationTime);
			forCar.setData(nextEdge);
			sendEvent(forCar);
			variabileRSU.updateMessaggiInviatiRSU_Veicolo();

			// se sono la destinazione non fare niente
			if (destinationOfVehicle.equals(nodo)) {
				// System.out.println("DESTINAZIONE");
				return;
			}

		} else if (nameMessage.equals("CAMBIO FASE")) {
			regolatore.nextPhase();
			variabileRSU.updateMessaggiInviatiRSU_RSU();
			variabileRSU.updateMessaggiRicevutiRSU_RSU();
			variabileRSU.getNumeroMessInviatiRSU_RSU();
		} else if (nameMessage.equals("DESTINAZIONE")) {
			Vehicle v = (Vehicle) m.getSource();
			// rimuovo il veicolo dal grafo
			((CityGraph) grafo).rimuoviVeicolo(v);
			((CityGraph) grafo).removeMobileNode(v);
			v.addAttribute("ui.style", "fill-color: rgba(0,0,0,100);");
			v.addAttribute("ui.label", "");
			// aggiorno statistiche
			variabileRSU.updateMessaggiRicevutiRSU_Veicolo();
			VariabileStatVeicolo variabileVeicolo = (VariabileStatVeicolo) m.getData()[1];
			try {
				// invio statistiche al server
				serverStatistiche.updateStatisticheVeicolo(variabileVeicolo);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			if (listaAutoIncrocio.contains(v)) {
				area.append("\n" + this + " veicolo N° " + v.getId() + " è arrivato a DESTINAZIONE!");
				scrollPane.getViewport().setViewPosition(new Point(0, area.getDocument().getLength()));
			}
			listaAutoIncrocio.remove(v);
		}

	}

	private void routing() {

		// lista di tutti i percorsi per la destinazione
		LinkedList<HashMap<NetNode, Path>> listaMappe = new LinkedList<>();
		for (NetEdge e : archiUscenti) {

			listaMappe.add(makeRoutingTable(e));
			// System.out.println(this+ " arco rimanente : "+e+"\nmakerouting table \n"
			// +makeRoutingTable(e)+"\n");
		}

		NetNode nodoDest = null;
		Path path = null;
		LinkedList<Path> listaPath = null;
		// ci giriamo le mappe della lista
		for (HashMap<NetNode, Path> mappa : listaMappe) {

			// per ogni mappa carchiamo le chiavi ovvero i nodi destinazione
			for (Entry<NetNode, Path> e : mappa.entrySet()) {

				nodoDest = e.getKey();
				path = e.getValue();
				if (path == null) continue;
				// se il nodo destinazione è già presente nella routing table aggiungiamo solo gli archi
				// alla listaArchi
				if (routingTable.containsKey(nodoDest)) {
					routingTable.get(nodoDest).add(path);
				}

				// altrimenti creaimo la listaArchi, aggiungiamo l'arco che porta a destinazione e
				// inseriamo nodoDestinazione e lista
				else {
					listaPath = new LinkedList<>();
					listaPath.add(path);
					routingTable.put(nodoDest, listaPath);
				}

			}

		}
		// ordiniamo i percorsi verso la destinazione in modo crescente
		for (Entry<NetNode, LinkedList<Path>> e : routingTable.entrySet()) {
			Collections.sort(e.getValue());
		}
		/*
		 * print* System.out.println(this+"------- "+routingTable); /
		 **/

	}

	private HashMap<NetNode, Path> makeRoutingTable(NetEdge arcoDaRimanere) {

		@SuppressWarnings("unchecked")
		ArrayList<NetEdge> archiDaRimuovere = (ArrayList<NetEdge>) archiUscenti.clone();
		archiDaRimuovere.remove(arcoDaRimanere);

		if (arcoDaRimanere == null) archiDaRimuovere = new ArrayList<>();

		HashMap<NetNode, NetEdge> routingTable = new HashMap<>();

		HashMap<NetNode, Path> routingTablePath = new HashMap<>();

		int[] pred = Dijkstra.getSpanningTree((NetNode) nodo, archiDaRimuovere);

		/*
		 * print* toString(pred)); /
		 **/

		int myIndex = nodo.getIndex();

		String id1;
		String id2;
		int curr;
		int prev;
		NetEdge nextHop = null;
		Path path = new Path();
		Graph graph = grafo;
		try {
			for (int i = 0; i < graph.getNodeCount(); i++) {
				// if the node "i" it's me don't do anything
				if (i == myIndex) continue;

				path = new Path();
				curr = i;
				prev = pred[curr];
				while (prev != myIndex) {
					if (prev == curr && curr != myIndex) break;
					// aggiungo l'arco nel path
					id1 = graph.getNode(prev).getId();
					id2 = graph.getNode(curr).getId();
					path.add(graph.getEdge(id1 + "-" + id2));

					curr = prev;
					prev = pred[curr];
				}
				if (prev == myIndex) {
					path.add(arcoDaRimanere);

				}
				// save the edge (from me to neighbour) needed to reach the node i
				id1 = graph.getNode(prev).getId();
				id2 = graph.getNode(curr).getId();
				nextHop = graph.getEdge(id1 + "-" + id2);
				routingTable.put(graph.getNode(i), nextHop);
				if (path.size() > 0) {
					routingTablePath.put(graph.getNode(i), path);
				}
				// TODO: handle exception
			}
		} catch (IndexOutOfBoundsException e) {
			// go to source from node i

		}
		/*
		 * print*
		 * System.out.println("GestorePrenotazioni. makeRoutingTab. "+this+" "+routingTable); /
		 **/
		return routingTablePath;
	}

	private synchronized NetEdge scegliProssimoArco(NetNode destination, NetEdge arcoDaEscludere) {

		NetEdge arcoScelto = null;
		if (destination.equals(nodo)) { return null; }
		LinkedList<Path> percorsiDestinazione = routingTable.get(destination);

		// tra tutti gli archi che portano a destinazione
		for (Path path : percorsiDestinazione) {
			if (arcoDaEscludere != null
							&& path.getFirstEdge().getTargetNode().equals(arcoDaEscludere.getSourceNode())) {
				variabileRSU.updateNumeroIndirizzamenti();
				continue;
			}

			arcoScelto = path.getFirstEdge();
			variabileRSU.updateNumeroIndirizzamentiSuPercorsoMinimo();
			variabileRSU.updateNumeroIndirizzamenti();
			/*
			 * print* System.out.println("\n"+this+": scelgo il prossimo nodo: "+nodoScelto);
			 * System.out.println("------ arco da escludere -> "+arcoDaEscludere); /
			 **/
			return arcoScelto;
		}

		return null;
	}

	@Override
	public void sendEvent(Event event) {
		nodo.sendEvent(event);

	}

	/////// SET E GET///////////////////////
	@Override
	public String toString() {
		return "RSU [nodo " + nodo.getId() + "]";
	}

	public NetGraph getGraph() {
		return grafo;
	}

	@Override
	public Scheduler getScheduler() {
		return nodo.getScheduler();
	}

	public NetNode getNetNode() {
		return nodo;
	}

	public int getID() throws RemoteException {
		return ID;
	}

	@Override
	public String getNameRSU() throws RemoteException {
		return "RSU [nodo " + nodo.getId() + "]";
	}

}