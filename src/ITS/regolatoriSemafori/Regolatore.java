package ITS.regolatoriSemafori;

import java.util.ArrayList;
import ITS.RSU.RSU;
import ITS.regolatoriSemafori.ABS_Regolatore.Fase;
import network.NetEdge;
import network.message.Message;
import simEventiDiscreti.Entity;
import simEventiDiscreti.Event;

public interface Regolatore {
	public enum Type {CLASSICO, INTELLIGENTE}
	
	public static Regolatore getType(Type type, RSU rsu, ArrayList<NetEdge> archiEntranti){
		switch(type){
		
		case CLASSICO : return new RegolatoreClassico(rsu, archiEntranti);
		
		case INTELLIGENTE : return new RegolatoreASoglia(rsu, archiEntranti);  
		
		default: throw new IllegalArgumentException("Il tipo di regolatore specificato non esiste");
		}
	}
	
	RSU getRSU(); //restituisce l'rsu a cui è stato assegnato
	Fase nextPhase(); //cambia la fase (disattivando la vecchia e attivando la nuova) e la restituisce
	void init(); //assegna le fasi (e altro)
//	public void handler(Event message);

}
