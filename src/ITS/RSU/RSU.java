package ITS.RSU;

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
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import ITS.regolatoriSemafori.Regolatore;
import ITS.regolatoriSemafori.RegolatoreClassico;
import ITS.regolatoriSemafori.RegolatoreIntelligente;
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
import statistiche.Variabile;
import util.Param;
import util.Path;
import vanet.Vehicle;

public class RSU extends Thread implements Entity, RemoteRSU {

	private NetNode nodo;
	private NetGraph grafo;
	private LinkedList<Vehicle> listaAutoIncrocio = new LinkedList<Vehicle>();
	private int ID;
	// tabella di routing con la lista degli archi per arrivare a destinazione
	private HashMap<NetNode, LinkedList<Path>> routingTable = new HashMap<>();
	private ArrayList<NetEdge> archiEntranti, archiUscenti;
	private ServerStatistiche serverStatistiche;
	private Variabile variabile;
	private Regolatore regolatore;
	private static final boolean regolatoreClassico = true;


	public RSU(Node node, int i) {
		
		ID = i;
		nodo = (NetNode) node;
		grafo = nodo.getGraph();
		variabile = new Variabile(this, ID);
		/* print */
		System.out.println("Creato " + this + " ID= "+ID);
		/**/
		
	}
	
	

	@Override
	public void run() {
		/* print */
		System.out.println("inizializzazione  " +this+" "+ getName());
		/**/
		init();
		while(true) {
			try {
				serverStatistiche.updateStatistiche(this,  variabile);
				Thread.sleep(1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
			//Se non ci sono più auto in circolazione posso uscire
			if(((CityGraph)grafo).getNodiInMovimento().size()==0) break;
		}

		try {
			serverStatistiche.richiestaStatisticheGenerali();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
	}

	// METHODS ////////////////////////////
	
	@Override
	public void stampaStatistiche(String statistica) throws RemoteException {
		System.out.println(this+ "ho ricevuto dal server statistiche:" + statistica);
	}

	public synchronized void init() {
		variabile.numVeicoliTot(((CityGraph)grafo).getNumVeicoliTot());
		
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
		if(regolatoreClassico)
			regolatore = new RegolatoreClassico(this, archiEntranti);
		else
			regolatore = new RegolatoreIntelligente(this, archiEntranti);
		regolatore.init();
		
		routing();
		
		try {
			UnicastRemoteObject.exportObject(this, 1098);
			serverStatistiche = (ServerStatistiche) Naming.lookup("Server");
			
			//iscrivo l'RSU alla lista del serverStatistiche
			serverStatistiche.registraRSU(this);
		} catch (MalformedURLException | NotBoundException  | RemoteException e) {
			e.printStackTrace();
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
		 * print*
		 * System.out.println("GestorePrenotazioni.makeroutingtable. spanningTree = "+Arrays.
		 * toString(pred)); /
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
			if (arcoDaEscludere != null && path.getFirstEdge().getTargetNode().equals(arcoDaEscludere.getSourceNode()))
				continue;

			arcoScelto = path.getFirstEdge();
			/*
			 * print* System.out.println("\n"+this+": scelgo il prossimo nodo: "+nodoScelto);
			 * System.out.println("------ arco da escludere -> "+arcoDaEscludere); /
			 **/
			return arcoScelto;
		}

		return null;
	}

	public void handler(Event message) {
		if (!(message instanceof Message))
			throw new IllegalArgumentException("the event sended at " + this + " is not a message");
		
		Message m = (Message) message;
		String nameMessage = m.getName();
		
		if (nameMessage.equals("ARRIVA MACCHINA")) {
			variabile.updateMessaggiRicevutiRSU_RSU();
			Vehicle v = (Vehicle) m.getData()[0];
			listaAutoIncrocio.add(v);
		}

		else if (nameMessage.equals("CAMBIO ARCO")) {
			variabile.updateMessaggiRicevutiRSU_Veicolo();
			variabile.updateDistanzaCoperta((double)m.getData()[2]);
			// indirizza auto
			Vehicle vehicle = (Vehicle) m.getSource();
			NetEdge nextEdge = scegliProssimoArco(vehicle.getTargetNode(), vehicle.getCurrentEdge());
			listaAutoIncrocio.remove(vehicle);

			// se è null vuol dire che la macchina è arrivata a destinazine
			if (nextEdge != null) {
				Message toRsu = new Message("ARRIVA MACCHINA", nodo, nextEdge.getTargetNode(), Param.elaborationTime);
				toRsu.setData(vehicle);
				sendEvent(toRsu);
				variabile.updateMessaggiInviatiRSU_RSU();
			}
			// System.out.println(this+" arrivato cambio arco da "+vehicle);
			// comunica all'auto in quale arco è stata indirizzata
			Message direzione = new Message("DIREZIONE", nodo, vehicle, Param.elaborationTime);
			direzione.setData(nextEdge);
			sendEvent(direzione);
			variabile.updateMessaggiInviatiRSU_Veicolo();
		}

		else if (nameMessage.equals("RICHIESTA PERCORSO")) {
			variabile.updateMessaggiRicevutiRSU_Veicolo();
			variabile.updateVeicoliRSU();
			Vehicle vehicle = (Vehicle) m.getSource();
			NetNode destinationOfVehicle = (NetNode) m.getData()[0];
			((CityGraph) grafo).setNodoInMovimento(vehicle);

			// System.out.println("\n"+this+": richiesta percorso di "+vehicle+" verso
			// "+destinationOfVehicle);
			NetEdge nextEdge = scegliProssimoArco(destinationOfVehicle, vehicle.getCurrentEdge());

			// System.out.println("------ arco consigliato "+nextEdge);

			// comunica all'auto su quale arco è stata indirizzata
			Message forCar = new Message("DIREZIONE", nodo, vehicle, Param.elaborationTime);
			forCar.setData(nextEdge);
			sendEvent(forCar);
			variabile.updateMessaggiInviatiRSU_Veicolo();

			// se sono la destinazione non fare niente
			if (destinationOfVehicle.equals(nodo)) {
				// System.out.println("DESTINAZIONE");
				return;
			}

		} else if (nameMessage.equals("CAMBIO FASE")) {
			variabile.updateMessaggiInviatiRSU_RSU();
			variabile.updateMessaggiRicevutiRSU_RSU();
			regolatore.nextPhase();
			variabile.getNumeroMessInviatiRSU_RSU();
		} else if (nameMessage.equals("DESTINAZIONE")) {
			Vehicle v = (Vehicle) m.getSource();
			variabile.updateMessaggiRicevutiRSU_Veicolo();
			variabile.updateTempoDiAttesa((double)m.getData()[2]);
			
			// rimuovo il veicolo dal grafo
			((CityGraph) grafo).rimuoviVeicolo(v);
			((CityGraph) grafo).removeMobileNode(v);
			v.addAttribute("ui.style", "fill-color: rgba(0,0,0,100);");
			v.addAttribute("ui.label", "");
		} 

	}

	///////SET E GET///////////////////////
	@Override
	public String toString() {
		return "RSU[nodo " + nodo.getId() + "]";
	}

	@Override
	public void sendEvent(Event event) {
		nodo.sendEvent(event);

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
		return "RSU[nodo " + nodo.getId() + "]";
	}
	

}