package ITS.RSU;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import ITS.regolatoriSemafori.Regolatore;
import network.NetEdge;
import network.NetNode;
import network.algorithm.Dijkstra;
import network.message.Message;
import simEventiDiscreti.Event;
import util.Param;
import util.Path;
import vanet.CityGraph;
import vanet.MobileNode;
import vanet.Vehicle;

public class RSU extends NetNode implements Runnable{

	// macchine nel raggio d'azione dell'RSU
	private ArrayList<Vehicle> nearbyVehicle = new ArrayList<>(20);

	private ArrayList<NetEdge> archiEntranti, archiUscenti;
	//tabella di routing con la lista degli archi per arrivare a destinazione
	private HashMap<NetNode, LinkedList<Path>> routingTable = new HashMap<>();
		
	// regola il verde ai semafori
	private Regolatore regolatore;
	private Regolatore.Type tipoRegolatore = Param.tipoRegolatore;

	// private StatRSU stat = new StatRSU(this);

	// COSTR //////////////////////////////
	public RSU(CityGraph graph, String name) {
		super(graph, name);

	}

	@Override
	public void run() {
		init();
//		while(getScheduler().getStart()) {
//			pingToVehicle();
//		}
	}

	// METHODS ////////////////////////////

	// una volta completato il grafo inizializza semafori,colonia e tabelle di routing
	public void init() {
		archiUscenti = new ArrayList<>(10);
		archiEntranti = new ArrayList<>(10);

		for (Edge edge : getGraph().getEachEdge()) {
			// preparo gli archi uscenti da assegnare alla colonia
			if (edge.getSourceNode().equals(this)) {
				archiUscenti.add((NetEdge) edge);
			}
			// preparo gli archi entranti da assegnare al regolatore dei semafori
			else if (edge.getTargetNode().equals(this)) {
				archiEntranti.add((NetEdge) edge);
			}
		}

		// genera regolatore dei semafori
		regolatore = Regolatore.getType(tipoRegolatore, this, archiEntranti);
		regolatore.init();

		routing();

		// inizializza ping loop
		// sendEvent(new EntityCreation("ENTITY CREATION", 0, this));
		// sendEvent(new Message("PING", this, this, Param.pingTime));
	}
	
	public synchronized void routing(){
		
		//lista di tutti i percorsi per la destinazione
		LinkedList<HashMap<NetNode, Path>> listaMappe = new LinkedList<>();
		for(NetEdge e : archiUscenti){
			
			listaMappe.add(makeRoutingTable(e));
	//		System.out.println(this+ " arco rimanente : "+e+"\nmakerouting table \n" +makeRoutingTable(e)+"\n");
		}
		
		NetNode nodoDest = null;
		Path path = null;
		LinkedList<Path> listaPath = null;
		//ci giriamo le mappe della lista 
		for(HashMap<NetNode, Path> mappa : listaMappe){
			
			//per ogni mappa carchiamo le chiavi ovvero i nodi destinazione 
			for(Entry<NetNode, Path> e : mappa.entrySet()){
				
				nodoDest = e.getKey();
				path = e.getValue();
				if(path==null) continue;
				//se il nodo destinazione è già presente nella routing table aggiungiamo solo gli archi alla listaArchi
				if(routingTable.containsKey(nodoDest)){
					routingTable.get(nodoDest).add(path);
				}
				
				//altrimenti creaimo la listaArchi, aggiungiamo l'arco che porta a destinazione e inseriamo nodoDestinazione e lista
				else{
					listaPath = new LinkedList<>();
					listaPath.add(path);
					routingTable.put(nodoDest, listaPath);
				}
				
				
			}
			
		}
		//ordiniamo i percorsi verso la destinazione in modo crescente
		for(Entry<NetNode, LinkedList<Path>> e : routingTable.entrySet()){
			Collections.sort(e.getValue());
		}
		/*print*
		System.out.println(this+"------- "+routingTable);
		/**/
		
	}
	
	
	private  HashMap<NetNode, Path> makeRoutingTable(NetEdge arcoDaRimanere){
	       
        @SuppressWarnings("unchecked")
		ArrayList<NetEdge> archiDaRimuovere = (ArrayList<NetEdge>) archiUscenti.clone();
        archiDaRimuovere.remove(arcoDaRimanere);
       
        if(arcoDaRimanere == null) archiDaRimuovere = new ArrayList<>();
       
        HashMap<NetNode, NetEdge> routingTable = new HashMap<>();
       
        HashMap<NetNode, Path> routingTablePath = new HashMap<>();
       
        int[] pred = Dijkstra.getSpanningTree(this,archiDaRimuovere);
       
        /*print*
        System.out.println("GestorePrenotazioni.makeroutingtable. spanningTree = "+Arrays.toString(pred));
        /**/
       
        int myIndex = getIndex();
       
        String id1; String id2;
        int curr; int prev;
        NetEdge nextHop = null;
        Path path = new Path();
        Graph graph = getGraph();
        try {
        for(int i=0; i<graph.getNodeCount(); i++){
            //if the node "i" it's me don't do anything
            if(i == myIndex)continue;
               
                path = new Path();
                curr = i;
                prev = pred[curr];
                while(prev != myIndex){
                    if(prev == curr && curr!=myIndex)break;
                    //aggiungo l'arco nel path
                    id1 = graph.getNode(prev).getId();
                    id2 = graph.getNode(curr).getId();
                    path.add(graph.getEdge(id1+"-"+id2));
                   
                    curr = prev;
                    prev = pred[curr];
                }
                if(prev == myIndex) {
                	path.add(arcoDaRimanere);
                	
                }
                //save the edge (from me to neighbour) needed to reach the node i
                id1 = graph.getNode(prev).getId();
                id2 = graph.getNode(curr).getId();
                nextHop = graph.getEdge(id1+"-"+id2);
                routingTable.put(graph.getNode(i), nextHop);
                if(path.size()>0) {
                	routingTablePath.put(graph.getNode(i), path);
                }
                // TODO: handle exception
            }
        } catch (IndexOutOfBoundsException e) {
            //go to source from node i
//          while(prev != myIndex){
           
        }
        /*print*
        System.out.println("GestorePrenotazioni. makeRoutingTab. "+this+" "+routingTable);
        /**/
        return routingTablePath;
    }
	

	public synchronized void pingToVehicle() {
		// search car in my range
		double carDistance;
		for (MobileNode car : ((CityGraph) getGraph()).getNodiInMovimento()) {
			carDistance = distance(car);
			// send ping message at the car in my range
			if (carDistance < Param.raggioRSU) {
				/*
				 * * System.out.println("rsu. handler. "+car+" Ã¨ nel raggio di "+this); /
				 **/
				long millisec = 10;
				getScheduler().addEvent(new Message("PING", this, car, millisec));

			} else {
				// lista veicoli registrati nell'RSU
				nearbyVehicle.remove(car);
			}
		}
		// colonia.evaporate();

	}

	@Override
	public synchronized void handler(Event message) {

		if (!(message instanceof Message))
			throw new IllegalArgumentException("the event sended at " + this + " is not a message");

		Message m = (Message) message;

		regolatore.readMessage(m);
//		colonia.readMessage(m);

		String nameMessage = m.getName();

		if (nameMessage.equals("PONG")) {

			// indirizza auto
			Vehicle vehicle = (Vehicle) m.getSource();
			NetEdge nextEdge = scegliProssimoArco(vehicle.getTargetNode(), vehicle.getCurrentEdge());


			// comunica all'auto in quale arco Ã¨ stata indirizzata
			Message direzione = new Message("DIREZIONE", this, vehicle, Param.elaborationTime);
			direzione.setData(nextEdge);
			sendEvent(direzione);
		}

		else if (nameMessage.equals("RICHIESTA PERCORSO")) {

			/**
			 * l'auto chiede il percorso verso la destinazione indirizza auto chiedendo alla colonia
			 */
			Vehicle vehicle = (Vehicle) m.getSource();
			NetNode destinationOfVehicle = (NetNode) m.getData()[0];

			/*
			 * print* System.out.println("\n"+this+": richiesta percorso di "+vehicle+" verso "
			 * +destinationOfVehicle); System.out.println("------ chiedo alla colonia -><-"); /
			 **/

			NetEdge nextEdge = scegliProssimoArco(destinationOfVehicle, vehicle.getCurrentEdge());

			/*
			 * print* System.out.println("------ arco consigliato "+nextEdge); /
			 **/

			// comunica all'auto su quale arco è stata indirizzata
			Message forCar = new Message("DIREZIONE", this, vehicle, Param.elaborationTime);
			forCar.setData(nextEdge);
			sendEvent(forCar);

			// se sono la destinazione non fare niente
			if (destinationOfVehicle.equals(this)) { return; }

			/*
			 * print*
			 * System.out.println("\n"+this+" invia prenotazione a: "+nextEdge.getTargetNode());
			 * System.out.println("--------- destinazione: "+destinationOfVehicle); /
			 **/

		}

	}
	
	private NetEdge scegliProssimoArco(NetNode destination, NetEdge arcoDaEscludere) {

		NetEdge nodoScelto = null;
		if(destination.equals(this)){return null;}
		LinkedList<Path> percorsiDestinazione = routingTable.get(destination);

		// tra tutti gli archi che portano a destinazione
		for (Path path : percorsiDestinazione) {
			if (arcoDaEscludere != null && path.getFirstEdge().getTargetNode().equals(arcoDaEscludere.getSourceNode()))
				continue;

			nodoScelto = path.getFirstEdge();
			/*
			 * print*
			 * System.out.println("\n"+this+": scelgo il prossimo nodo: "+nodoScelto);
			 * System.out.println("------ arco da escludere -> "+arcoDaEscludere); /
			 **/
			return nodoScelto;
		}

		return null;
	}

	private double distance(MobileNode car) {
		double xRSU = getAttribute("x");
		double yRSU = getAttribute("y");
		double xCAR = car.getX();
		double yCAR = car.getY();

		double x = xRSU - xCAR;
		double y = yRSU - yCAR;

		double distance = Math.sqrt((x * x) + (y * y));
		return Math.abs(distance);

	}

	@Override
	public String toString() {
		return "RSU[" + getId() + "]";
	}

}