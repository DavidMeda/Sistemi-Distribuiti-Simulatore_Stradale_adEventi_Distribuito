package ITS.semafori;

import ITS.regolatoriSemafori.Regolatore;
import network.NetEdge;

public interface Semaforo {
	
	static enum Type{classico,intelligente}
	
	static Semaforo getType(Type type, Regolatore regolatore, NetEdge edge){
		switch(type){
		case classico: return new SemaforoClassico(regolatore, edge);
		case intelligente: return new SemaforoIntelligente(regolatore, edge);
		
		default: throw new IllegalArgumentException("type "+type+" not exist");
		}
	}
	
	double setVerde(); //restituisce anche la durata del verde
	void setRosso();
	void sempreVerde();
	
	NetEdge getEdge();
	
}
