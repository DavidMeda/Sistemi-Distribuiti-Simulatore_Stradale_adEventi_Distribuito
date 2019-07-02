package ITS.semafori;

import java.util.LinkedList;
import ITS.RSU.RSU;
import ITS.regolatoriSemafori.Regolatore;
import network.NetEdge;
import network.message.Message;
import util.Param;
import vanet.Vehicle;

public abstract class ABS_Semaforo implements Semaforo {
	protected boolean verde = false;
	protected LinkedList<Vehicle> coda = null;
	
	//arco associato al semaforo
	protected NetEdge edge = null;
	protected boolean sempreVerde = false;
	
	//gestore messaggi
//	protected MessageManager messageManager = null;
	
	//regolatore a cui è stato assegnato il semaforo
	protected Regolatore regolatore = null;
	
	// COSTR ///////////////////////
	public ABS_Semaforo(Regolatore regolatore, NetEdge edge) {
		this.edge = edge;
		this.regolatore = regolatore;
	}
	
	// OVERRIDE /////////////
	//from semaforo

	@Override
	public NetEdge getEdge() {
		return edge;
	}
	
	protected void avvisaVeicoli(String messaggio){
		Message msg = null;
		RSU sourceRSU = regolatore.getRSU();
		/*print*
		System.out.println(this+": messaggio "+messaggio+" alla coda del semaforo = "+coda);
		/**/
		for(Vehicle v : coda){
			msg = new Message(messaggio, sourceRSU, v, Param.elaborationTime);
			sourceRSU.sendEvent(msg);
		}
	}
	
	public void sempreVerde() {
		sempreVerde = true;
	}
	
	//from obj
	@Override
	public String toString(){
		return "Semaforo su "+edge.getTargetNode()+" da "+edge.getSourceNode();
	}
	
	
	
	
}
