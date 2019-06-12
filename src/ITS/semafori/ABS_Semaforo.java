package ITS.semafori;

import ITS.regolatoriSemafori.Regolatore;
import network.NetEdge;

public abstract class ABS_Semaforo implements Semaforo {
	protected boolean verde = false;
	
	//arco associato al semaforo
	protected NetEdge edge = null;
	
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
	public boolean isVerde() {
		return verde;
	}

	@Override
	public boolean isRosso() {
		return !verde;
	}

	@Override
	public NetEdge getEdge() {
		return edge;
	}
	
	//from obj
	@Override
	public String toString(){
		return "Semaforo su "+edge.getTargetNode()+" da "+edge.getSourceNode();
	}
	
	
	
	
}
