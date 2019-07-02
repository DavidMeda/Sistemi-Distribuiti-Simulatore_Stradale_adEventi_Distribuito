package ITS.semafori;

import ITS.regolatoriSemafori.Regolatore;
import network.NetEdge;
import vanet.CityGraph;

public class SemaforoClassico extends ABS_Semaforo{
	//lista delle auto sull'arco
//	private LinkedList<Vehicle> coda = null;
	
	//tempo di verde in millesecondi
	private final double tempoDiVerde = 1000;
	
	
	// COSTR /////////////////////
	public SemaforoClassico(Regolatore regolatore, NetEdge edge) {
		super(regolatore,edge);
		CityGraph g = (CityGraph)regolatore.getRSU().getGraph();
		coda = g.getVehiclesOnTheEdge(edge.getId());
	}
	
	// OVERRIDE //////////////////
	//from abs_semaforo
	@Override
	public double setVerde() {
		verde = true;
		avvisaVeicoli("VERDE");
		
		return tempoDiVerde;
	}
//	private void avvisaVeicoli(String messaggio){
//		Message msg = null;
//		RSU sourceRSU = regolatore.getRSU();
//		/*print*
//		if(source.getId().equals("A")) {
//		System.out.println(this+": messaggio "+messaggio+" alla coda del semaforo = "+coda);
//		}
//		/**/
//		for(Vehicle v : coda){
//			msg = new Message(messaggio, sourceRSU, v, Param.elaborationTime);
//			sourceRSU.sendEvent(msg);
//		}
//	}

	@Override
	public void setRosso() {
		if(!sempreVerde) {
			verde = false;
			avvisaVeicoli("ROSSO");
		}
		else {
			setVerde();
//			System.out.println("Semaforo classico - SEMPRE VERDE");
		}
	}

	@Override
	public NetEdge getEdge() {
		return edge;
	}

	

	

}
