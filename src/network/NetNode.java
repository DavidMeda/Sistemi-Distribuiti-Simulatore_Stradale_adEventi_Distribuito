package network;

import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.AbstractGraph;
import org.graphstream.graph.implementations.MultiNode;
import ITS.RSU.RSU;
import simEventiDiscreti.Entity;
import simEventiDiscreti.Event;
import simEventiDiscreti.Scheduler;

public class NetNode extends MultiNode implements Entity {
//	private Scheduler scheduler;
	private RSU rsu;
	
	/////////////////////////////////////////////////////
	public NetNode(NetGraph graph, String name) {
		super((AbstractGraph)graph, name);
		
	}
	
	
	public void init() {
		rsu = new RSU(this);
		rsu.init();
	}
	
	/////////////////////////////////////////////////////////
	//from node
	@Override
	public int getIndex(){
		//se non lo fai non funziona
		int i = 0;
		for(Node n : getGraph().getEachNode()){
			if(this.equals(n))return i;
			i++;
		}
		
		return i;
	}
	//from entity
	@Override
	public void handler(Event event){
//		HashMap<NetNode,RSU> mappaNodoRSU =  ((NetGraph)getGraph()).getMappa();
//		for(Entry<NetNode, RSU> e : mappaNodoRSU.entrySet()) {
//			if(e.getKey().equals(this)) {
//				e.getValue().handler(event);
//			}
//		}
		rsu.handler(event);
	}

//	@Override
//	public void setScheduler(Scheduler scheduler) {
//		this.scheduler = scheduler;
//	}
	
	@Override
	public Scheduler getScheduler() {
//		return scheduler;
		return ((NetGraph)getGraph()).getScheduler();
	}

	@Override
	public void sendEvent(Event event) {
		/* *
		if(event.getName().equals("ROUND ROBIN")){
			System.out.println("netnode. sendevent. "+event.getName());
			System.out.println("                  . round robin inviato a "+((Message)event).getDestination());
		}
		/**/
		
		getScheduler().addEvent(event);
		
		
	}
	public void ping() throws InterruptedException {rsu.pingToVehicle();}
	//from object
	@Override
	public boolean equals(Object netNode){
		if(!(netNode instanceof NetNode))return false;
		NetNode n = (NetNode)netNode;
		
		boolean vero = getGraph().getId().equals(n.getGraph().getId());
		boolean falso = getId().equals(n.getId());

		/*print*
		System.out.println("netnode. equals. io="+this+" l'altro="+n);
		System.out.println("               . mioGrafo="+getGraph().getId()+" suoGrafo="+n.getGraph().getId());
		System.out.println("               . mioid="+getId()+" suoid="+n.getId());
		System.out.println("               . "+(vero&&falso));
		/**/
		
		return vero && falso; //challenge accepted
	}
	
	
}
	