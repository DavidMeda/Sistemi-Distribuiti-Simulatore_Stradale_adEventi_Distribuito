package network.algorithm;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import network.CityGraph;
import network.NetEdge;
import network.NetGraph;
import network.NetNode;

public class Dijkstra {

	public static NetNode[] getMinimumPath(NetNode from, NetNode to) {
		NetGraph g = (NetGraph) from.getGraph();

		int[] spanningTree = getSpanningTree(from);

		LinkedList<NetNode> buff = new LinkedList<>();

		int curr = to.getIndex();
		int prev = 0;

		do {
			buff.addFirst(g.getNode(curr));

			prev = curr;
			curr = spanningTree[prev];
		} while (curr != prev);

		return (NetNode[]) buff.toArray();

	}

	public static int[] getSpanningTree(NetNode from) {

		NetGraph rete = (NetGraph) from.getGraph();

		NetNode[] nodi = new NetNode[rete.getNodeCount()];
		int source = 0;
		NetNode node;
		for (int i = 0; i < nodi.length; i++) {
			node = rete.getNode(i);
			if (from.equals(node)) {
				source = i;
			}
			nodi[i] = node;

		}

		Arco[] archi = new Arco[rete.getEdgeCount()];

		for (int i = 0; i < archi.length; i++) {
			NetEdge edge = rete.getEdge(i);
			double length = edge.getAttribute("length");

			NetNode inizio = rete.getEdge(i).getSourceNode();
			NetNode fine = rete.getEdge(i).getTargetNode();

			// search node for get number id
			int id1 = 0;
			int id2 = 0;
			for (int j = 0; j < nodi.length; j++) {
				if (nodi[j].equals(inizio)) {
					id1 = j;
				} else if (nodi[j].equals(fine)) {
					id2 = j;
				}
			}

			archi[i] = new Dijkstra.Arco(id1, id2, length);

		}

		Grafo grafo = new Grafo(archi);

		return grafo.calculateSpanningTreeOf(source);

	}

	public static int[] getSpanningTree(NetNode from, List<NetEdge> archiDaEliminare) {

		NetGraph reteVera = (NetGraph) from.getGraph();

		NetGraph rete = new CityGraph("");

		for (Node n : reteVera.getEachNode()) {
			rete.addNode(n.getId());
		}

		NetEdge nuovoArco;

		// if(from.getId().equals("I"))
		// System.out.println(archiDaEliminare);

		a: for (Edge e : reteVera.getEachEdge()) {
			nuovoArco = new NetEdge(e.getId(), e.getSourceNode(), e.getTargetNode(), e.isDirected());

			for (NetEdge arco : archiDaEliminare) {

				if (nuovoArco.getSourceNode().getId().equals(arco.getTargetNode().getId())
								&& nuovoArco.getTargetNode().getId().equals(arco.getSourceNode().getId())
								|| nuovoArco.getId().equals(arco.getId())) {
					// if(from.getId().equals("I"))
					// System.out.println("sono qui "+nuovoArco);
					continue a;
				}
			}
			nuovoArco = (NetEdge) rete.addEdge(e.getSourceNode().getId(), e.getTargetNode().getId());
			nuovoArco.addAttribute("length", (double) e.getAttribute("length"));

		}

		NetNode[] nodi = new NetNode[rete.getNodeCount()];

		int source = 0;
		NetNode node;
		for (int i = 0; i < nodi.length; i++) {
			node = rete.getNode(i);
			if (from.getId().equals(node.getId())) {
				source = i;
			}
			nodi[i] = node;

		}

		Arco[] archi = new Arco[rete.getEdgeCount()];

		for (int i = 0; i < archi.length; i++) {
			NetEdge edge = rete.getEdge(i);
			// if(from.getId().equals("I"))
			// System.out.println("Edge "+edge);
			// if(archiDaEliminare.contains(edge)) {
			// syso
			// continue;
			// }
			double length = edge.getAttribute("length");

			NetNode inizio = rete.getEdge(i).getSourceNode();
			NetNode fine = rete.getEdge(i).getTargetNode();

			// search node for get number id
			int id1 = 0;
			int id2 = 0;
			for (int j = 0; j < nodi.length; j++) {
				if (nodi[j].equals(inizio)) {
					id1 = j;
				} else if (nodi[j].equals(fine)) {
					id2 = j;
				}
			}

			archi[i] = new Dijkstra.Arco(id1, id2, length);

		}

		Grafo grafo = new Grafo(archi);

		int[] pred = grafo.calculateSpanningTreeOf(source);
		// if(from.getId().equals("A"))
		// System.out.println("pre "+Arrays.toString(pred));

		return pred;

	}

	static class Arco {

		private int fromNodeIndex;

		public int getFromNodeIndex() {
			return fromNodeIndex;
		}

		private int toNodeIndex;

		public int getToNodeIndex() {
			return toNodeIndex;
		}

		private double length;

		public double getLength() {
			return length;
		}

		public Arco(int fromNodeIndex, int toNodeIndex, double length) {
			this.fromNodeIndex = fromNodeIndex;
			this.toNodeIndex = toNodeIndex;
			this.length = length;
		}

		/**
		 * Determines the neighbouring node of a supplied node, based on the 2 nodes connected by
		 * this edge.
		 * 
		 * @param nodeIndex The index of one of the nodes that this edge joins.
		 * @return The index of the neighbouring node.
		 *
		 */
		public int getNeighbourIndex(int nodeIndex) {
			if (this.fromNodeIndex == nodeIndex) {
				return this.toNodeIndex;
			} else {
				return this.fromNodeIndex;
			}
		}

	}
	///////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////

	static class Grafo {

		private Nodo[] nodes;

		public Nodo[] getNodes() {
			return nodes;
		}

		private int noOfNodes;

		public int getNoOfNodes() {
			return noOfNodes;
		}

		private Arco[] edges;

		public Arco[] getEdges() {
			return edges;
		}

		private int noOfEdges;

		public int getNoOfEdges() {
			return noOfEdges;
		}

		/**
		 * Constructor that builds the whole graph from an Array of Edges
		 */
		public Grafo(Arco[] edges) {

			// The edges are passed in, so store them
			this.edges = edges;

			// Create all the nodes, ready to be updated with the edges
			this.noOfNodes = calculateNoOfNodes(edges);
			this.nodes = new Nodo[this.noOfNodes];
			for (int n = 0; n < this.noOfNodes; n++) {
				this.nodes[n] = new Nodo();
			}

			// Add all the edges to the nodes. Each edge is added to 2 nodes (the "to" and the "from")
			this.noOfEdges = edges.length;
			for (int edgeToAdd = 0; edgeToAdd < this.noOfEdges; edgeToAdd++) {
				try {
					this.nodes[edges[edgeToAdd].getFromNodeIndex()].getEdges().add(edges[edgeToAdd]);
					this.nodes[edges[edgeToAdd].getToNodeIndex()].getEdges().add(edges[edgeToAdd]);

				} catch (NullPointerException e) {
					// TODO: handle exception
				}
			}

			/*
			 * TODO* System.out.println("grafo .costr. "); for(int i=0; i<this.edges.length;
			 * i++)System.out.print("-"+this.edges[i]); System.out.println();
			 * System.out.println("#edges="+edges.length+" #nodes"+nodes.length); for(int i=0;
			 * i<this.nodes.length; i++)System.out.print("-"+nodes[i]); System.out.println(); /
			 **/

		}

		/**
		 * Calculate the number of nodes in an array of edges
		 * 
		 * @param edges An array of edges that represents the graph.
		 * @return The number of nodes in the graph.
		 *
		 */
		private int calculateNoOfNodes(Arco[] edges) {
			int noOfNodes = 0;
			for (Arco e : edges) {
				if (e == null) continue;
				if (e.getToNodeIndex() > noOfNodes) noOfNodes = e.getToNodeIndex();
				if (e.getFromNodeIndex() > noOfNodes) noOfNodes = e.getFromNodeIndex();
			}
			noOfNodes++;
			return noOfNodes;
		}

		/**
		 * Uses Dijkstra's algorithm to calculate the shortest distance from the source to all
		 * nodes
		 * 
		 */
		// public int[] calculateShortestDistances() {
		public int[] calculateSpanningTreeOf(int n) {
			int[] pred = new int[nodes.length];

			for (int i = 0; i < pred.length; i++) {
				pred[i] = i;
			}
			// Set node 0 as the source
			this.nodes[n].setDistanceFromSource(0);
			int nextNode = n;

			// Visit every node, in order of stored distance
			for (int i = 0; i < this.nodes.length; i++) {

				// Loop round the edges that are joined to the current node
				ArrayList<Arco> currentNodeEdges = this.nodes[nextNode].getEdges();

				for (int joinedEdge = 0; joinedEdge < currentNodeEdges.size(); joinedEdge++) {

					// Determine the joined edge neighbour of the current node
					int neighbourIndex = currentNodeEdges.get(joinedEdge).getNeighbourIndex(nextNode);

					// Only interested in an unvisited neighbour
					if (!this.nodes[neighbourIndex].isVisited()) {

						// Calculate the tentative distance for the neighbour
						double tentative = this.nodes[nextNode].getDistanceFromSource()
										+ currentNodeEdges.get(joinedEdge).getLength();

						// Overwrite if the tentative distance is less than what's currently stored
						if (tentative < nodes[neighbourIndex].getDistanceFromSource()) {
							nodes[neighbourIndex].setDistanceFromSource(tentative);
							pred[neighbourIndex] = nextNode;
						}

					}

				}

				// All neighbours are checked so this node is now visited
				nodes[nextNode].setVisited(true);

				// The next node to visit must be the one with the shortest distance.
				nextNode = getNodeShortestDistanced();

			}
			/*
			 * TODO* for(int i=0;i<pred.length;i++)
			 * System.out.println("grafo. pred di "+i+" "+pred[i]); /
			 **/
			return pred;
		}

		/**
		 * Scans the unvisited nodes and calculates which one has the shortest distance from the
		 * source.
		 * 
		 * @return The index of the node with the smallest distance
		 */
		private int getNodeShortestDistanced() {

			int storedNodeIndex = 0;
			double storedDist = Integer.MAX_VALUE;

			for (int i = 0; i < this.nodes.length; i++) {
				double currentDist = this.nodes[i].getDistanceFromSource();
				if (!this.nodes[i].isVisited() && currentDist < storedDist) {
					storedDist = currentDist;
					storedNodeIndex = i;
				}

			}

			return storedNodeIndex;
		}

		/**
		 * Overrides Object.toString() to show the contents of the graph
		 * 
		 */
		public String toString() {

			String output = "Number of nodes = " + this.noOfNodes;
			output += "\nNumber of edges = " + this.noOfEdges;

			for (int i = 0; i < this.nodes.length; i++) {
				output += ("\nThe shortest distance from node 0 to node " + i + " is "
								+ nodes[i].getDistanceFromSource());
			}

			return output;

		}

	}
	///////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////

	static class Nodo {

		private double distanceFromSource = Integer.MAX_VALUE;

		public double getDistanceFromSource() {
			return distanceFromSource;
		}

		public void setDistanceFromSource(double distanceFromSource) {
			this.distanceFromSource = distanceFromSource;
		}

		private boolean visited = false;

		public boolean isVisited() {
			return visited;
		}

		public void setVisited(boolean visited) {
			this.visited = visited;
		}

		private ArrayList<Arco> edges = new ArrayList<Arco>();

		public ArrayList<Arco> getEdges() {
			return edges;
		}

		public void setEdges(ArrayList<Arco> edges) {
			this.edges = edges;
		}

	}
}
