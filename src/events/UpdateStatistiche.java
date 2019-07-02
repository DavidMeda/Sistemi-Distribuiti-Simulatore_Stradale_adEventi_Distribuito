package events;

import simEventiDiscreti.Event;
import vanet.CityGraph;

public class UpdateStatistiche extends Event{
	private CityGraph graph;
	
	public UpdateStatistiche(CityGraph graph, double amongManyMillisec) {
		super("UPDATE STATISTICHE", amongManyMillisec);
		this.graph = graph;
		
	}
	
	@Override
	public void action() {
		graph.updateStatistiche();
		
		
	}

}
