package events;

import org.graphstream.graph.Node;
import network.NetNode;
import simEventiDiscreti.Event;
import vanet.CityGraph;

public class Ping extends Event {
	private CityGraph grafo;
	
	public Ping(CityGraph graph, double amongManyMillisec) {
		super("PING", amongManyMillisec);
		grafo = graph;
	}

	@Override
	public void action()  {
		for(Node n : grafo.getEachNode()) {
			try {
				((NetNode)n).ping();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
