package vanet;


import network.NetEdge;
import network.NetNode;
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
		case "PING":
			/*print*/
			System.out.println(this+" ha ricevuto ping da "+message.getSource());
			/**/
			
			
			if(message.getSource() == registeredRSU)return;
			
			sendEvent(new Message("PONG", this, message.getSource(), Param.elaborationTime));
			registeredRSU = (NetNode)message.getSource();
			
			break;
		
			
		/********/		
		case "VERDE":
			/*print*/
			System.out.println(this+": è arrivato il verde dal semaforo di "+message.getSource());			
			/**/
			verdeAlSemaforo = true;
			moving = true;
			break;
		
		
		/********/		
		case "ROSSO":
			/*print*/
			System.out.println(this+": Ã¨ arrivato il rosso dal semaforo di "+message.getSource());
			/**/

			verdeAlSemaforo = false;
			moving = false;
			break;	
			
			
		/********/		
		case "DIREZIONE":
			path.add((NetEdge)message.getData()[0]);


			/*print*/
			System.out.println("\n"+this+": aggiunto "+(NetEdge)message.getData()[0]+" al percorso");
			/**/
			
			break;
		
			
		/********/		
		case "START":
			/*print*/
			System.out.println("\n"+this+" START!!! destinazione "+destinationNode);
			/**/
			moving = true;
			registeredRSU = getCurrentNode();
			
			getGraph().setNodoInMovimento(this);
			getPathTo(destinationNode);
			
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
