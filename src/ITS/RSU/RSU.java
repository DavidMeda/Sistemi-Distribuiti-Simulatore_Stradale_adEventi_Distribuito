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
import network.NetEdge;
import network.NetGraph;
import network.NetNode;
import network.algorithm.Dijkstra;
import network.message.Message;
import simEventiDiscreti.Entity;
import simEventiDiscreti.Event;
import simEventiDiscreti.Scheduler;
import statistiche.ServerStatistiche;
import statistiche.Variabili;
import util.Param;
import util.Path;
import vanet.CityGraph;
import vanet.Vehicle;

public class RSU extends Thread implements Entity, RemoteRSU {

	private NetNode nodo;
	private NetGraph grafo;
	private LinkedList<Vehicle> listaAutoIncrocio = new LinkedList<Vehicle>();
	private int ID;
	// macchine nel raggio d'azione dell'RSU
	// private ArrayList<Vehicle> nearbyVehicle = new ArrayList<>(20);

	private ArrayList<NetEdge> archiEntranti, archiUscenti;
	private ArrayList<NetNode> nodiEntranti, nodiUscenti;
	// tabella di routing con la lista degli archi per arrivare a destinazione
	private HashMap<NetNode, LinkedList<Path>> routingTable = new HashMap<>();
	private ServerStatistiche serverStat;
	private Variabili var;
	// regola il verde ai semafori
	private Regolatore regolatore;
	// private Regolatore.Type tipoRegolatore = Param.tipoRegolatore;
	// private Semaphore mutex = new Semaphore(1);

	// COSTR //////////////////////////////
	public RSU(Node node, int i) {
		ID = i;
		nodo = (NetNode) node;
		grafo = nodo.getGraph();
		var = new Variabili(this, ID);
		/* print */
		System.out.println("Creato " + this + "");
		/**/
	}
	
	

	@Override
	public void run() {
		/* print */
		System.out.println("inizializzazione  " + getName());
		/**/
		init();
		while(true) {
			try {
				serverStat.updateStatistiche(this,  var);
//				System.out.println(getNameRSU()+" update statistiche");
				Thread.sleep(1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(((CityGraph)grafo).getNodiInMovimento().size()==0) break;
		}
		System.out.println("--------SIMULAZIONE FINITA!--------");
		try {
			System.out.println(serverStat.statisticheGenerali());
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	// METHODS ////////////////////////////

	public synchronized void init() {
		var.numVeicoliTot(((CityGraph)grafo).getNumVeicoliTot());
		
		archiUscenti = new ArrayList<>(10);
		archiEntranti = new ArrayList<>(10);
		nodiUscenti = new ArrayList<>(10);
		nodiEntranti = new ArrayList<>(10);

		for (Edge edge : grafo.getEachEdge()) {
			// preparo gli archi uscenti
			if (edge.getSourceNode().equals(nodo)) {
				archiUscenti.add((NetEdge) edge);
				nodiUscenti.add((NetNode) edge.getSourceNode());
			}
			// preparo gli archi entranti da assegnare al regolatore dei semafori
			else if (edge.getTargetNode().equals(nodo)) {
				archiEntranti.add((NetEdge) edge);
				nodiEntranti.add((NetNode) edge.getTargetNode());
			}
		}
		// genera regolatore dei semafori
		regolatore = new RegolatoreClassico(this, archiEntranti);
		regolatore.init();
		routing();
		try {

			UnicastRemoteObject.exportObject(this, 1098);
			try {
				serverStat = (ServerStatistiche) Naming.lookup("Server");
			} catch (MalformedURLException | NotBoundException e) {
				e.printStackTrace();
			}

			serverStat.registraRSU(this);
		} catch (RemoteException e) {
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
			// while(prev != myIndex){

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

		// regolatore.handler(m);
		// colonia.readMessage(m);

		String nameMessage = m.getName();
		if (nameMessage.equals("ARRIVA MACCHINA")) {
			Vehicle v = (Vehicle) m.getData()[0];
			listaAutoIncrocio.add(v);
			var.updateMessaggiRicevutiRSU_RSU();
		}

		if (nameMessage.equals("CAMBIO ARCO")) {
			var.getNumeroMessRicevutiRSU_Veicolo();
			// indirizza auto
			Vehicle vehicle = (Vehicle) m.getSource();
			NetEdge nextEdge = scegliProssimoArco(vehicle.getTargetNode(), vehicle.getCurrentEdge());
			listaAutoIncrocio.remove(vehicle);

			// se è null vuol dire che soo arrivato a destinazine
			if (nextEdge != null) {
				Message toRsu = new Message("ARRIVA MACCHINA", nodo, nextEdge.getTargetNode(), Param.elaborationTime);
				toRsu.setData(vehicle);
				sendEvent(toRsu);
				var.updateMessaggiInviatiRSU_RSU();
			}
			// System.out.println(this+" arrivato cambio arco da "+vehicle);
			// comunica all'auto in quale arco Ã¨ stata indirizzata
			Message direzione = new Message("DIREZIONE", nodo, vehicle, Param.elaborationTime);
			direzione.setData(nextEdge);
			sendEvent(direzione);
			var.updateMessaggiInviatiRSU_Veicolo();
		}

		else if (nameMessage.equals("RICHIESTA PERCORSO")) {
			var.updateMessaggiRicevutiRSU_Veicolo();
			var.updateVeicoliRSU();
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
			var.getNumeroMessInviatiRSU_Veicolo();
//			Message updateStatistiche = new Message("STATISTICHE", this, this, Param.elaborationTime);
//			sendEvent(updateStatistiche);

			// se sono la destinazione non fare niente
			if (destinationOfVehicle.equals(nodo)) {
				// System.out.println("DESTINAZIONE");
				return;
			}

		} else if (nameMessage.equals("CAMBIO FASE")) {
			regolatore.nextPhase();
			var.getNumeroMessInviatiRSU_RSU();
		} else if (nameMessage.equals("DESTINAZIONE")) {
			Vehicle v = (Vehicle) m.getSource();
			var.updateMessaggiRicevutiRSU_Veicolo();
			// rimuovo il veicolo dal grafo
			// try {
			// mutex.acquire();
			// } catch (InterruptedException e) {
			// e.printStackTrace();
			// }
			((CityGraph) grafo).rimuoviVeicolo(v);
			((CityGraph) grafo).removeMobileNode(v);
			// mutex.release();
			v.addAttribute("ui.style", "fill-color: rgba(0,0,0,100);");
			v.addAttribute("ui.label", "");
//			statistiche();
//			try {
//				System.out.println(serverStat.statisticheGenerali());
//			} catch (RemoteException e) {
//				e.printStackTrace();
//			}
		} 
//		else if (nameMessage.equals("STATISTICHE")) {
//			try {
//				serverStat.updateStatistiche(ID, "NUMERO AUTO", numMessaggi);
//				serverStat.updateStatistiche(ID, "NUMERO MESSAGGI", numMessaggi);
//
//			} catch (RemoteException e) {
//				e.printStackTrace();
//			}
//		}

	}

//	public void statistiche() {
//		try {
//			serverStat.updateStatistiche(this, "NUMERO AUTO", numAuto);
//			serverStat.updateStatistiche(this, "NUMERO MESSAGGI", numMessaggi);
////			System.out.println(this+ "update");
//		} catch (RemoteException e) {
//			e.printStackTrace();
//		}
//	}

	
	
	
	// METODI PER LE STATISTICHE //////////
	
		public double getCongestioneMediaArchi() {
			double gradoCongestione = 0.0;
			
			double gradoCongestioneArcoCorrente = 0.0;
			double lunghezzaArcoCorrente = 0.0;
			double capacitaArcoCorrente = 0.0;
			double numeroVeicoliSuArcoCorrente = 0.0;
			for(NetEdge e : archiEntranti) {
				lunghezzaArcoCorrente = (double)e.getAttribute("length");
				
				capacitaArcoCorrente = (int)(lunghezzaArcoCorrente / Param.distanzaInterveicolo);
				numeroVeicoliSuArcoCorrente = ((CityGraph)grafo).getVehiclesOnTheEdge(e.getId()).size();
				
				gradoCongestioneArcoCorrente = (numeroVeicoliSuArcoCorrente / capacitaArcoCorrente);
				
				gradoCongestione += gradoCongestioneArcoCorrente;
			}
			
			return gradoCongestione / archiEntranti.size();
		}
		
		public double getCongestione() {
			double veicoliPresentiSugliArchi = 0.0;
			double capacitaArchi = 0.0;
			
			double veicoliSuArcoCorrente = 0.0;
			double lunghezzaArcoCorrente = 0.0;
			for(NetEdge e : archiEntranti) {
				veicoliSuArcoCorrente = ((CityGraph)grafo).getVehiclesOnTheEdge(e.getId()).size();
				lunghezzaArcoCorrente = (double)e.getAttribute("length");
				
				veicoliPresentiSugliArchi += veicoliSuArcoCorrente;
				capacitaArchi += (lunghezzaArcoCorrente / Param.distanzaInterveicolo);
			}
			
			return veicoliPresentiSugliArchi / capacitaArchi;
			
		}


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

	@Override
	public void stampaStatistiche(double statistica) throws RemoteException {
//		System.out.println("Il server mi ha mandato " + statistica);

	}

	public int getID() throws RemoteException {
		return ID;
	}
	
	@Override
	public String getNameRSU() throws RemoteException {
		return "RSU[nodo " + nodo.getId() + "]";
	}

}