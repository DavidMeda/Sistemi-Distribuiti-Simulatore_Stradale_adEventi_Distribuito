package ITS.regolatoriSemafori;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import ITS.RSU.RSU;
import ITS.semafori.Semaforo;
import ITS.semafori.SemaforoIntelligente;
import network.NetEdge;
import network.NetNode;
import network.message.Message;
import simEventiDiscreti.Event;
import simEventiDiscreti.Scheduler;
import vanet.CityGraph;

public class RegolatoreIntelligente extends ABS_Regolatore{
	

	public RegolatoreIntelligente(RSU rsu, List<NetEdge> archiEntranti) {
		super(rsu, archiEntranti);
	}
	
	public synchronized void init() {
		//assegna un semaforo per ogni fase
		Semaforo s = null;
		LinkedList<Semaforo> ls = null;
		ArrayList<Fase> fasi = new ArrayList<>(semafori.size());
		for(Entry<NetNode, ArrayList<Semaforo>> e : semafori.entrySet()){
			s = e.getValue().get(0);
			ls = new LinkedList<>(); 
			ls.add(s);
			fasi.add(new Fase(s));
		}
		
		/*print*
		System.out.println(this+": init.");
		System.out.println("\t fasi= "+fasi.size());
		/**/
		setFasi(fasi);

//		//inizia il ciclo delle fasi
//		Message start = new Message("CAMBIO FASE", sourceRSU, sourceRSU, 0);
//		sourceRSU.sendEvent(start);
		super.init();
		nextPhase();
		
		
	}

	public double getCongestione(SemaforoIntelligente semaforo){
		//restituisce il grado di congestione dell'arco rispetto al nodo
		NetEdge edge = semaforo.getEdge();
		double veicoliTotali = 0;
		double veicoliArco = ((CityGraph)sourceRSU.getGraph()).getVehiclesOnTheEdge(edge.getId()).size();
		
		for(Entry<NetNode, ArrayList<Semaforo>> e : semafori.entrySet()){
			for(Semaforo s : e.getValue()){
				edge = s.getEdge();
				veicoliTotali += ((CityGraph)sourceRSU.getGraph()).getVehiclesOnTheEdge(edge.getId()).size();
			}
		}
		
		/*print*
		if(edge.getTargetNode().getId().equals("O")){
		System.out.println("------ veicoliArco = "+veicoliArco+" veicoliTot = "+veicoliTotali);
		System.out.println("------ gradoCalcolato = "+(veicoliArco / veicoliTotali));
		}
		/**/
		
		return veicoliArco / veicoliTotali;
	
	}

	

}
