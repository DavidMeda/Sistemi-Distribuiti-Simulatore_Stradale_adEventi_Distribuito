package ITS.semafori;

import ITS.regolatoriSemafori.Regolatore;
import network.CityGraph;
import network.NetEdge;

public class SemaforoClassico extends ABS_Semaforo{
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
