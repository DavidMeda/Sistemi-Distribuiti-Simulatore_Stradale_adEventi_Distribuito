package ITS.RSU;

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
import util.Param;
import util.Path;
import vanet.CityGraph;
import vanet.Vehicle;

public class RSU extends Thread implements Entity {

	private NetNode nodo;
	private NetGraph grafo;
	// macchine nel raggio d'azione dell'RSU
	// private ArrayList<Vehicle> nearbyVehicle = new ArrayList<>(20);

	private ArrayList<NetEdge> archiEntranti, archiUscenti;
	// tabella di routing con la lista degli archi per arrivare a destinazione
	private HashMap<NetNode, LinkedList<Path>> routingTable = new HashMap<>();

	// regola il verde ai semafori
	private Regolatore regolatore;
	// private Regolatore.Type tipoRegolatore = Param.tipoRegolatore;
	// private Semaphore mutex = new Semaphore(1);

	// private StatRSU stat = new StatRSU(this);

	// COSTR //////////////////////////////
	public RSU(Node node) {
		nodo = (NetNode) node;
		grafo = nodo.getGraph();
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
		// while(node.getScheduler().getStart()) {
		// try {
		// pingToVehicle();
		// Thread.sleep(100);
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }
	}

	// METHODS ////////////////////////////

	// una volta completato il grafo inizializza semafori,colonia e tabelle di routing
	public synchronized void init() {
		archiUscenti = new ArrayList<>(10);
		archiEntranti = new ArrayList<>(10);

		for (Edge edge : grafo.getEachEdge()) {
			// preparo gli archi uscenti da assegnare alla colonia
			if (edge.getSourceNode().equals(nodo)) {
				archiUscenti.add((NetEdge) edge);
			}
			// preparo gli archi entranti da assegnare al regolatore dei semafori
			else if (edge.getTargetNode().equals(nodo)) {
				archiEntranti.add((NetEdge) edge);
			}
		}
		// genera regolatore dei semafori
		regolatore = new RegolatoreClassico(this, archiEntranti);
		regolatore.init();
		routing();
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

//	public synchronized void pingToVehicle() throws InterruptedException {
//		 // search car in my range
//		 // a.acquire();
//		 double carDistance;
//		// mutex.acquire();
//		 for (MobileNode car : ((CityGraph) node.getGraph()).getNodiInMovimento()) {
//		 carDistance = distance(car);
//		 // send ping message at the car in my range
//		 if (carDistance < Param.raggioRSU) {
//		 getScheduler().addEvent(new Message("PING", node, car, Param.elaborationTime));
//		 /*print*/
//		 System.out.println(this+" invio ping a "+car);
//		 /**/
//		 } else {
//		 // lista veicoli registrati nell'RSU
//		 nearbyVehicle.remove(car);
//		 }
//		 }
//		 mutex.release();
//		 a.release();
//		 colonia.evaporate();
//	}

	
	public void handler(Event message) {

		if (!(message instanceof Message))
			throw new IllegalArgumentException("the event sended at " + this + " is not a message");

		Message m = (Message) message;

		// regolatore.handler(m);
		// colonia.readMessage(m);

		String nameMessage = m.getName();

		if (nameMessage.equals("CAMBIO ARCO")) {
			// indirizza auto
			Vehicle vehicle = (Vehicle) m.getSource();
			NetEdge nextEdge = scegliProssimoArco(vehicle.getTargetNode(), vehicle.getCurrentEdge());

			// System.out.println(this+" arrivato cambio arco da "+vehicle);
			// comunica all'auto in quale arco Ã¨ stata indirizzata
			Message direzione = new Message("DIREZIONE", nodo, vehicle, Param.elaborationTime);
			direzione.setData(nextEdge);
			sendEvent(direzione);
		}

		else if (nameMessage.equals("RICHIESTA PERCORSO")) {
			/*
			 * l'auto chiede il percorso verso la destinazione indirizza auto chiedendo alla colonia
			 */
			Vehicle vehicle = (Vehicle) m.getSource();
			NetNode destinationOfVehicle = (NetNode) m.getData()[0];
			// try {
			// mutex.acquire();
			// } catch (InterruptedException e) {
			// e.printStackTrace();
			// }
			((CityGraph) grafo).setNodoInMovimento(vehicle);
			// mutex.release();

			// System.out.println("\n"+this+": richiesta percorso di "+vehicle+" verso
			// "+destinationOfVehicle);
			NetEdge nextEdge = scegliProssimoArco(destinationOfVehicle, vehicle.getCurrentEdge());

			// System.out.println("------ arco consigliato "+nextEdge);

			// comunica all'auto su quale arco è stata indirizzata
			Message forCar = new Message("DIREZIONE", nodo, vehicle, Param.elaborationTime);
			forCar.setData(nextEdge);
			sendEvent(forCar);

			// se sono la destinazione non fare niente
			if (destinationOfVehicle.equals(nodo)) {
				// System.out.println("DESTINAZIONE");
				return;
			}

		} else if (nameMessage.equals("CAMBIO FASE")) {
			regolatore.nextPhase();
		} else if (nameMessage.equals("ARRIVATO")) {
			Vehicle v = (Vehicle) m.getSource();
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
		}

	}

	private synchronized NetEdge scegliProssimoArco(NetNode destination, NetEdge arcoDaEscludere) {

		NetEdge nodoScelto = null;
		if (destination.equals(nodo)) { return null; }
		LinkedList<Path> percorsiDestinazione = routingTable.get(destination);

		// tra tutti gli archi che portano a destinazione
		for (Path path : percorsiDestinazione) {
			if (arcoDaEscludere != null && path.getFirstEdge().getTargetNode().equals(arcoDaEscludere.getSourceNode()))
				continue;

			nodoScelto = path.getFirstEdge();
			/*
			 * print* System.out.println("\n"+this+": scelgo il prossimo nodo: "+nodoScelto);
			 * System.out.println("------ arco da escludere -> "+arcoDaEscludere); /
			 **/
			return nodoScelto;
		}

		return null;
	}

	// private double distance(MobileNode car) {
	// double xRSU = nodo.getAttribute("x");
	// double yRSU = nodo.getAttribute("y");
	// double xCAR = car.getX();
	// double yCAR = car.getY();
	//
	// double x = xRSU - xCAR;
	// double y = yRSU - yCAR;
	//
	// double distance = Math.sqrt((x * x) + (y * y));
	// return Math.abs(distance);
	//
	// }

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

}