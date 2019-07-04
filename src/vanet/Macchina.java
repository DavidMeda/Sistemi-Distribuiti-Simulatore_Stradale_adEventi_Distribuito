package vanet;


import network.CityGraph;
import network.NetEdge;
import network.message.Message;
import simEventiDiscreti.Event;
import util.Param;

public class Macchina extends Vehicle{
	
	// COSTR //////////////////////////////
	public Macchina(){}
	public Macchina(CityGraph graph, String name) {
		super(graph, name);
		
	}
	

	
	// OVERRIDE /////////////////////
	//from entity
	@Override
	public void handler(Event event) {
		
		Message message = null;
		if(event instanceof Message){
			message = (Message)event;
		}
		
		
		switch(event.getName()){
		
		/********/	
//		case "PING":
//			/*print*
//			System.out.println(this+" ha ricevuto ping da "+message.getSource());
//			/**/
//			
//			
//			if(message.getSource() == registeredNode)return;
//			
//			sendEvent(new Message("PONG", this, message.getSource(), Param.elaborationTime));
//			
//			registeredNode = (NetNode)message.getSource();
//			break;
		
			
		/********/		
		case "VERDE":
			/*print*
			System.out.println(this+": è arrivato il verde dal semaforo di "+message.getSource());			
			/**/
			verdeAlSemaforo = true;
			moving = true;
			break;
		
		
		/********/		
		case "ROSSO":
			/*print*
			System.out.println(this+": è arrivato il rosso dal semaforo di "+message.getSource());
			/**/

			verdeAlSemaforo = false;
			moving = false;
			break;	
			
			
		/********/		
		case "DIREZIONE":
			NetEdge nextEdge = (NetEdge)message.getData()[0];
			path.add(nextEdge);
			if(nextEdge != null) setCurrentNode(nextEdge.getTargetNode());

			/*print
			System.out.println("\n"+this+": aggiunto "+nextEdge+" al percorso");
			/**/
			
			
			break;
		
			
		/********/		
		case "START":
			/*print*
			System.out.println("\n"+this+" START!!! destinazione "+destinationNode);
			/**/
			moving = true;
			registeredNode = getCurrentNode();
			
//			getGraph().setNodoInMovimento(this);
//			getPathTo(destinationNode);
			path.clear();
			
			/*print*
			System.out.println("\n"+this+": invio richiesta percorso a "+currentNode +" per la destinazione "+destinationNode);
			/**/
			
			Message askPath = new Message("RICHIESTA PERCORSO", this, currentNode, Param.elaborationTime); 
			
			askPath.setData(destinationNode);
			sendEvent(askPath);
			break;
			
		}
	
	}
	@Override
	public String toString(){
		return "Macchina["+getId()+"]";
	}
	
	////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////
	
//	public static void main(String[] args) {
//	
//	}

}
