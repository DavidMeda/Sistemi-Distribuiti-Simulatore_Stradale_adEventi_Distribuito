package network;

import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.AbstractGraph;
import org.graphstream.graph.implementations.MultiNode;
import ITS.RSU.RSU;
import simEventiDiscreti.Entity;
import simEventiDiscreti.Event;
import simEventiDiscreti.Scheduler;

public class NetNode extends MultiNode implements Entity {
	private RSU rsu;
	private NetGraph graph;
	private static int idRSU = 0;
	/////////////////////////////////////////////////////
	public NetNode(NetGraph graph, String name) {
		super((AbstractGraph)graph, name);
		this.graph = graph;
		
	}
	
	public String toString() {
		return "Incrocio ["+getId()+"]";
	}
	public void init() {
		idRSU++;
		rsu = new RSU(this, idRSU);
		rsu.start();
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
		rsu.handler(event);
	}

	@Override
	public Scheduler getScheduler() {
		return graph.getScheduler();
	}

	@Override
	public void sendEvent(Event event) {
		graph.getScheduler().addEvent(event);
	}
	
	public RSU getRsu() {
		return rsu;
	}

	public void setRsu(RSU rsu) {
		this.rsu = rsu;
	}

	public NetGraph getGraph() {
		return graph;
	}

	public void setGraph(NetGraph graph) {
		this.graph = graph;
	}
	//from object
	@Override
	public boolean equals(Object netNode){
		if(!(netNode instanceof NetNode))return false;
		NetNode n = (NetNode)netNode;
		
		boolean vero = graph.getId().equals(n.getGraph().getId());
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
	