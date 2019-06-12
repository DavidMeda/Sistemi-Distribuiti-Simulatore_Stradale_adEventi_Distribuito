package ITS.semafori;

import ITS.regolatoriSemafori.Regolatore;
import network.NetEdge;

public interface Semaforo {
	
	static enum Type{classico,quasiIntelligente,intelligente, edgeCongestion}
	
	static Semaforo getType(Type type, Regolatore regolatore, NetEdge edge){
		switch(type){
		case classico: return new SemaforoClassico(regolatore, edge);
//		case quasiIntelligente: return new SemaforoQuasiIntelligente(regolatore, edge);
		case intelligente: return new SemaforoIntelligente(regolatore, edge);
//		case edgeCongestion: return new SemaforoEdgeCongestion(regolatore, edge);
		
		default: throw new IllegalArgumentException("type "+type+" not exist");
		}
	}
	
	double setVerde(); //restituisce anche la durata del verde
	void setRosso();
	
	boolean isVerde();
	boolean isRosso();
	
	NetEdge getEdge();
//	MessageManager getMessageManager();
	
}
