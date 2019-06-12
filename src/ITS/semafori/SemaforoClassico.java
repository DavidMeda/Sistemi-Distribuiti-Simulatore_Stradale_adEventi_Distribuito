package ITS.semafori;

import java.util.LinkedList;
import ITS.RSU.RSU;
import ITS.regolatoriSemafori.Regolatore;
import network.NetEdge;
import network.message.Message;
import util.Param;
import vanet.CityGraph;
import vanet.Vehicle;

public class SemaforoClassico extends ABS_Semaforo{
	//lista delle auto sull'arco
	private LinkedList<Vehicle> coda = null;
	
	//tempo di verde in millesecondi
	private final double tempoDiVerde = 1000;
	
	// COSTR /////////////////////
	public SemaforoClassico(Regolatore regolatore, NetEdge edge) {
		super(regolatore,edge);
		CityGraph g = (CityGraph)edge.getTargetNode().getGraph();
		coda = g.getVehiclesOnTheEdge(edge.getId());
	}
	
	// OVERRIDE //////////////////
	//from abs_semaforo
	@Override
	public double setVerde() {
		/*TODO*
		if(edge.getId().equals("H-P")){
			verde = true;
			avvisaVeicoli("VERDE");
			return 500;
		}
		if(edge.getId().equals("Q-P")){
			verde = true;
			avvisaVeicoli("VERDE");
			return 10000;
		}
		if(edge.getId().equals("N-O")){
			verde = true;
			avvisaVeicoli("VERDE");
			return 500;
		}
		if(edge.getId().equals("R-O")){
			verde = true;
			avvisaVeicoli("VERDE");
			return 10000;
		}
		/**/

		verde = true;
		avvisaVeicoli("VERDE");
		
		return tempoDiVerde;
	}
	private void avvisaVeicoli(String messaggio){
		Message msg = null;
		RSU sourceRSU = regolatore.getRSU();
		/*print*
		if(source.getId().equals("A")) {
		System.out.println(this+": messaggio "+messaggio+" alla coda del semaforo = "+coda);
		}
		/**/
		for(Vehicle v : coda){
			msg = new Message(messaggio, sourceRSU, v, Param.elaborationTime);
			sourceRSU.sendEvent(msg);
		}
	}

	@Override
	public void setRosso() {
		verde = false;
		avvisaVeicoli("ROSSO");
	}
	

	@Override
	public MessageManager getMessageManager() {
		return null;
	}
	
	

}
