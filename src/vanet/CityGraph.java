package vanet;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import org.graphstream.graph.Edge;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;
import ITS.RSU.RSU;
import events.Ping;
import events.UpdatePosition;
import network.NetEdge;
import network.NetGraph;
import network.NetNode;
import util.Param;

public class CityGraph extends NetGraph{
	// INSTANCE ///////////////////////
	
	private HashMap<NetEdge, LinkedList<Vehicle>> vehiclesAttached = new HashMap<>();
	private LinkedList<MobileNode> 
		mobileNodes = new LinkedList<>(),
		nodiInMovimento = new LinkedList<>();
	private SpriteManager vehicleManager;
//	private Statistiche statistiche = new Statistiche();
	private LinkedList<Vehicle> veicoliDaRimuovere = new LinkedList<>();
	public static int cont = 0;
	
	
	// COSTR ////////////////////////////////

	public CityGraph(String name){
		super(name);
		vehicleManager = new SpriteManager(this);
		
	}
//	public CityGraph(String name, Scheduler scheduler) {
//		super(name, scheduler);
//		vehicleManager = new SpriteManager(this);
//	
//	}
	
	
	
	// GETTER ///////////////////////////////////
	public LinkedList<MobileNode> getNodiInMovimento(){return nodiInMovimento;}
//	public Statistiche getStatistiche() {return statistiche;}
	public HashMap<NetEdge, LinkedList<Vehicle>> getVehicles(){return vehiclesAttached;}
	public LinkedList<MobileNode> getMobileNodes(){return mobileNodes;}
	public LinkedList<Vehicle> getVehiclesOnTheEdge(String idEdge){
		NetEdge e = getEdge(idEdge);
		return vehiclesAttached.get(e);
	}
	
//	public void setStatistiche(Statistiche statistiche){this.statistiche = statistiche;}
	
	// METHODS //////////////////////////////////
	
	public void ping() throws InterruptedException{
		for(Entry<NetNode, RSU> e : mappaNodoRSU.entrySet() ){
			e.getValue().pingToVehicle();
		}
		getScheduler().addEvent(new Ping(this, Param.pingTime));
		
	}
	public void setNodoInMovimento(MobileNode nodoMobile){
		mobileNodes.remove(nodoMobile);
		nodiInMovimento.add(nodoMobile);
		
	}
	
	public void rimuoviVeicolo(Vehicle veichle) {veicoliDaRimuovere.add(veichle);}
	
	public void updateMobileNode() {
		for(MobileNode n : nodiInMovimento) {
			n.move();
		}
		
		for(Vehicle v : veicoliDaRimuovere) {
			removeMobileNode(v);
			v = null;
			
		}
		
		getScheduler().addEvent(new UpdatePosition(this, Param.updatePositionTime));
	
	}
	
//	public void addStatistica(Stat stat) {statistiche.addStat(stat);}
	
	@Override
	public void init(){
		for(Edge e : getEachEdge()){
			vehiclesAttached.put((NetEdge)e, new LinkedList<>());
		}
		getScheduler().addEvent(new UpdatePosition(this, Param.updatePositionTime));
		getScheduler().addEvent(new Ping(this, Param.pingTime));

		super.init();
		
	}
	public void attachVehicleToEdge(String idEdge, Vehicle vehicle){
		NetEdge e = getEdge(idEdge);
		vehiclesAttached.get(e).add(vehicle);
		
		/*print*
		System.out.println("citygraph.attachtoedge. aggiunto "+vehicle+" all'arco "+idEdge+" --> "+vehiclesAttached.get(e));
		/**/
	}
	public boolean removeVehicleFromEdge(String idEdge, MobileNode mobileNode){
		NetEdge e = getEdge(idEdge);
		return vehiclesAttached.get(e).remove(mobileNode);
		
	}
	public Sprite addMobileNode(Object id, Class <? extends MobileNode> mobileNodeClass){
		/*print*
		System.out.println(this+": addMobileNode");
		System.out.println(this+": id nodo mobile = "+id.toString());
		System.out.println(this+": scheduler = "+getScheduler());
		/**/
		
		MobileNode s = vehicleManager.addSprite(id.toString(), mobileNodeClass);
//		s.setScheduler(getScheduler());
		s.setGraph(this).setID(id);
		mobileNodes.add(s);
		return s;
		
	}
	
	//arrivati a destinazione rimuovo i veicoli e li metto a null
	public void removeMobileNode(MobileNode mobileNode){
		NetEdge e = (NetEdge)mobileNode.getAttachment();
		
		removeVehicleFromEdge(e.getId(), mobileNode);
		mobileNodes.remove(mobileNode);
		nodiInMovimento.remove(mobileNode);
		vehicleManager.removeSprite(mobileNode.getId());
		mobileNode = null;
		if(mobileNodes.size() == 0 && nodiInMovimento.size()==0 )getScheduler().stopSimulation();
	}
	
	
	
	//return true if is full
	public boolean edgeIsFull(NetEdge edge){
		int numMaxVehicle = (int)((double)edge.getAttribute("length") / (Param.distanzaInterveicolo));
		
		int numVehicleEdge = vehiclesAttached.get(edge).size();
		return ! (numVehicleEdge < numMaxVehicle);
		
	}

}