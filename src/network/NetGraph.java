package network;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import org.graphstream.graph.Edge;
import org.graphstream.graph.EdgeFactory;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.NodeFactory;
import org.graphstream.graph.implementations.MultiGraph;
import ITS.RSU.RSU;
import simEventiDiscreti.Scheduler;
import util.Param;
import vanet.CityGraph;

public class NetGraph extends MultiGraph {
	
	protected HashMap<NetNode,RSU> mappaNodoRSU = new HashMap<NetNode,RSU>();

	// //////////////////////
	// node factory
	private static class NetNodeFactory implements NodeFactory<NetNode> {

		public NetNode newInstance(String name, Graph graph) {
			return new NetNode((CityGraph) graph, name);
		}
	}

	// edge factory
	private static class NetEdgeFactory implements EdgeFactory<NetEdge> {

		public NetEdge newInstance(String arg0, Node arg1, Node arg2, boolean arg3) {
			return new NetEdge(arg0, (NetNode) arg1, (NetNode) arg2, arg3);
		}

	}

	private String description;
	private Scheduler scheduler;

	// COSTR //////////////////////////
	public NetGraph(String name) {
		super(name);
		setNodeFactory(new NetNodeFactory());
		setEdgeFactory(new NetEdgeFactory());
		scheduler = new Scheduler(Param.orizzonteTemporaleSimulazione);

	}
	// public NetGraph(String name, Scheduler scheduler) {
	// super(name);
	// setNodeFactory(new NetNodeFactory());
	// setEdgeFactory(new NetEdgeFactory());
	// this.scheduler = scheduler;
	// }
	//////////////////////////////////////

	// SETTER /////////////////////////
	public NetGraph setDescription(String description) {
		this.description = description;
		return this;
	}

	// GETTER /////////////////////////
	public Scheduler getScheduler() {
		return scheduler;
	}

	public String getDescription() {
		return description;
	}
	
	public HashMap<NetNode,RSU> getMappa(){return mappaNodoRSU;}

	// METHODS ///////////////////
	public void resetScheduler() {
		scheduler = new Scheduler(Param.orizzonteTemporaleSimulazione);
	}

	public void init() {
		for (Node n : getEachNode()) {
			((NetNode)n).init();
//			RSU rsu = new RSU(n);
//			mappaNodoRSU.put(((NetNode)n), rsu);
//			rsu.start();
		}
	}

	public void startSimulation() throws InterruptedException {
		init();
		scheduler.start();
		scheduler.join();

	}

	public Edge addEdge(String node1, String node2) {
		String name = node1 + "-" + node2;

		NetNode a = new NetNode(this, node1);
		NetNode b = new NetNode(this, node2);

		return addEdge(name, a, b);

	}

	public boolean contains(String node) {
		Iterator<Node> it = getNodeIterator();
		Node n = null;
		for (; it.hasNext();) {
			n = it.next();

			if (n.getId().equals(node)) { return true; }
		}
		return false;
	}

	// OVERRIDE ////////////////////////////////////////////////////////
	// from abs graph
	@SuppressWarnings("unchecked")
	@Override
	public NetNode addNode(String id) {
		if (!contains(id)) {
			Node n = super.addNode(id);
			return (NetNode) n;
		}
		return null;
	}
	//////////////////////////////////////////////////////////

	// public static void main(String[] args) {
	// NetGraph n = new NetGraph("");
	//
	// }

}