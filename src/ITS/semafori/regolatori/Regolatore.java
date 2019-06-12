package ITS.semafori.regolatori;

import java.util.ArrayList;

import ITS.RSU;
import ITS.semafori.regolatori.ABS_Regolatore.Fase;
import network.NetEdge;
import network.message.Message;

public interface Regolatore {
	public enum Type {ITS, CLASSICO, INTELLIGENTE}
	
	public static Regolatore getType(Type type, RSU rsu, ArrayList<NetEdge> archiEntranti){
		switch(type){
		
		case CLASSICO : return new RegolatoreClassico(rsu, archiEntranti);
		
		case INTELLIGENTE : return new RegolatoreIntelligente(rsu, archiEntranti);  
		
		default: throw new IllegalArgumentException("Il tipo di regolatore specificato non esiste");
		}
	}
	
	RSU getRSU(); //restituisce l'rsu a cui è stato assegnato
	Fase nextPhase(); //cambia la fase (disattivando la vecchia e attivando la nuova) e la restituisce
	
	void readMessage(Message message); //gestisce i messaggi
	void init(); //assegna le fasi (e altro)

}
