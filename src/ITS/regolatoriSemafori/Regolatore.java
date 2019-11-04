package ITS.regolatoriSemafori;

import java.util.ArrayList;
import ITS.RSU.RSU;
import ITS.regolatoriSemafori.ABS_Regolatore.Fase;
import network.NetEdge;

public interface Regolatore {

	public enum Type {
		CLASSICO, INTELLIGENTE
	}

	public static Regolatore getType(Type type, RSU rsu, ArrayList<NetEdge> archiEntranti) {
		switch (type) {

		case CLASSICO:
			return new RegolatoreClassico(rsu, archiEntranti);

		case INTELLIGENTE:
			return new RegolatoreCodaCorta(rsu, archiEntranti);

		default:
			throw new IllegalArgumentException("Il tipo di regolatore specificato non esiste");
		}
	}

	RSU getRSU(); // restituisce l'rsu a cui � stato assegnato

	Fase nextPhase(); // cambia la fase (disattivando la vecchia e attivando la nuova) e la restituisce

	void init(); // assegna le fasi (e altro)

}
