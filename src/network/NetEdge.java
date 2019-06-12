package network;

import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.AbstractEdge;

public class NetEdge extends AbstractEdge{

	public NetEdge(String id, NetNode source, NetNode target, boolean directed) {
		super(id, source, target, directed);
	}
	@SuppressWarnings("unchecked")
	@Override
	public NetNode getTargetNode(){
		// i don't know why but the nodes in the edges are not the real nodes
		Node fakeTargetNode = super.getTargetNode();
		String idTargetNode = fakeTargetNode.getId();
		NetNode realNode = fakeTargetNode.getGraph().getNode(idTargetNode);
		return realNode;
	}
	@SuppressWarnings("unchecked")
	@Override
	public NetNode getSourceNode(){
		// i don't know why but the nodes in the edges are not the real node
		Node fakeSourceNode = super.getSourceNode();
		String idTargetNode = fakeSourceNode.getId();
		NetNode realNode = fakeSourceNode.getGraph().getNode(idTargetNode);
		return realNode;
	}
	@Override
	public boolean equals(Object o){
		if(!(o instanceof NetEdge))return false;
		NetEdge e = (NetEdge)o;
		boolean sameNode = e.getSourceNode().equals(getSourceNode()) && e.getTargetNode().equals(getTargetNode());
//		boolean sameLength = (Integer)(e.getAttribute("length")) == (Integer)(getAttribute("length")); 
		return  sameNode;
	}
	@Override
	public String toString(){
		return "["+getId()+"]";
	}


}
