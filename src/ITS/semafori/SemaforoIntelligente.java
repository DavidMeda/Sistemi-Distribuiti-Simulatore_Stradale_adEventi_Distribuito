package ITS.semafori;

import java.util.LinkedList;

import ITS.RSU;
import ITS.semafori.regolatori.Regolatore;
import ITS.semafori.regolatori.RegolatoreIntelligente;
import network.NetEdge;
import network.message.Message;
import util.Param;
import vanet.CityGraph;
import vanet.Vehicle;

public class SemaforoIntelligente extends ABS_Semaforo{
	private boolean verde = false;
	private LinkedList<Vehicle> coda = null;
	
//	private double velocitaVeicolo = Param.velocitaVeicolo;
	private double raggioAccettazione = Param.distanzaMinimaAutorizzazione;
	
	private double sogliaCongestione = 0.8;
	
	//soglia minima e massima del tempo di verde
	private double tempoMin, tempoMax;

	
	public SemaforoIntelligente(Regolatore regolatore, NetEdge edge) {
		super(regolatore,edge);
		CityGraph g = (CityGraph)edge.getTargetNode().getGraph();
		coda = g.getVehiclesOnTheEdge(edge.getId());
		
		//tempo per far arrivare all'incrocio l'auto all'interno del raggio
		tempoMin = (raggioAccettazione / Param.velocitaVeicolo) + 1.0;
				
		//tempo per far passare tutta la coda
		tempoMax = ((double)edge.getAttribute("length") / Param.velocitaVeicolo) - tempoMin;
	}
	
	// METHODS /////////////////
	private void avvisaVeicoli(String messaggio){
		Message msg = null;
		RSU source = regolatore.getRSU();
		
		for(Vehicle v : coda){
			msg = new Message(messaggio, source, v, Param.elaborationTime);
			source.sendEvent(msg);
		}
	}
	
	private boolean macchineVicine(){
		double lunghezzaArco = ((Double)edge.getAttribute("length"));
		double distanzaMinima = lunghezzaArco - ((Double)coda.getFirst().getPositionOnTheEdge());
		double distanzaVeicolo;
		for(Vehicle v : coda){
			distanzaVeicolo = lunghezzaArco - ((Double)v.getPositionOnTheEdge());
			if(distanzaMinima > distanzaVeicolo){
				distanzaMinima = distanzaVeicolo; 
			}
		}
		
		return distanzaMinima < raggioAccettazione;
	}
	
	// OVERRIDE ////////////////////
	@Override
	public double setVerde() {
		if(coda.isEmpty())return 0.0;
		
		if(!macchineVicine())return 0.0;
		
		//restituisce quanto contribuisce quest'arco a congestionare l'incrocio
		double gradoCongestione = ((double)((RegolatoreIntelligente)regolatore).getCongestione(this));
		
		//si da preferenza alle code che provocano meno congestione
		double percentuale =  ((double)1 - gradoCongestione);
		
		//passano tutte le auto se la coda è troppo corta
		if(percentuale > sogliaCongestione){
			percentuale = 1;
		}
		
		/*print*
		if(edge.getTargetNode().getId().equals("O")){
			
			System.out.println(this+": veicolo all'incrocio");
			System.out.println("----- gradoCongest="+gradoCongestione);
			System.out.println("----- percentuale="+percentuale);
			System.out.println("----- tempo di verde = "+(percentuale * tempoMax) + tempoMin);
			System.out.println("----- tempo minimo= "+tempoMin);
		}
		/**/
		double tempoDiVerde = ((double)(percentuale * tempoMax) + tempoMin);
		
		verde = true;
		avvisaVeicoli("VERDE");
		
		return tempoDiVerde;
	
	}

	@Override
	public void setRosso() {
		verde = false;
		avvisaVeicoli("ROSSO");
	}

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

	@Override
	public MessageManager getMessageManager() {
		return null;
	}
	
	

}
