package events;

import simEventiDiscreti.Event;
import network.CityGraph;

public class UpdatePosition extends Event{
	private CityGraph graph;
	
	public UpdatePosition(CityGraph graph, double amongManyMillisec) {
		super("UPDATE POSITION", amongManyMillisec);
		this.graph = graph;
	
	}
	
	@Override
	public void action() {
		graph.updateMobileNode();
		
		
	}

}
