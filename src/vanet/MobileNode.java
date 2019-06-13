package vanet;

import org.graphstream.ui.spriteManager.Sprite;
import network.message.Message;
import simEventiDiscreti.Entity;
import simEventiDiscreti.Event;
import simEventiDiscreti.Scheduler;

public abstract class MobileNode extends Sprite implements Entity{	
	
	private CityGraph graph;
//	private Scheduler scheduler;
	private String id;
	
	//////////////////////////////////////////
	
	public MobileNode(CityGraph graph, String name) {
		this.graph = graph;
		this.id = name;
		
		
	}

	public MobileNode(){}
	////////////////////////////////////////////
	
	// GETTER ///////////////////////////
	public String getId(){return id;}
	public CityGraph getGraph(){return graph;}
	
	// SETTER ///////////////////////
	public MobileNode setGraph(CityGraph graph){
		this.graph = graph; 
//		this.scheduler = graph.getScheduler();
		return this;
	
	}
	public MobileNode setID(Object name){
		id = name.toString();
		return this;
		
	}
	///////////// METHODS /////////////////////
	public void beginsToMoveAt(double millisec){
		/* *
		System.out.println("mobilenode. "+this+" begintomove in "+millisec);
		/**/
		sendEvent(new Message("START", this, this, millisec));
		
	}
	
	
	////////////// ABSTRACT ////////////////////
	
	public abstract void move();
	
	/////////////// OVERRIDE ///////////////////
	//from entity
	@Override
	public void sendEvent(Event event) {
		graph.getScheduler().addEvent(event);
	
	}
	@Override
	public Scheduler getScheduler(){return graph.getScheduler();}
	
}