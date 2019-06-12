package events;

import simEventiDiscreti.Event;
import vanet.CityGraph;

public class Ping extends Event {
	private CityGraph grafo;
	
	public Ping(CityGraph graph, double amongManyMillisec) {
		super("PING", amongManyMillisec);
		grafo = graph;
	}

	@Override
	public void action() {
		grafo.ping();
	}

}
