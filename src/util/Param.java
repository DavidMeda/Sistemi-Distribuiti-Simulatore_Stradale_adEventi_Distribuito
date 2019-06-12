package util;

import java.util.Random;
import ITS.regolatoriSemafori.Regolatore;
import ITS.semafori.Semaforo;

public final class Param {
	/** descrizione [unità di misura] */
	
	/*/* parametri di simulazione /**/
	
	public static void setOrizzonteTemporale(double durataSimulazione) {orizzonteTemporaleSimulazione = durataSimulazione;}
	public static double orizzonteTemporaleSimulazione = 0; //durata simulazione (0 = infinito) [millisec] 

	public static long seed = 666;
	public static Random random = new Random(seed);
	public static void resetRandom() {random = new Random(seed);}
	public static void setSeed(long seed) {
		Param.seed = seed;
		random = new Random(seed);
	}
//	public static void setPeriodoGenerazione(double periodoGenerazione){
//		Param.periodoGenerazione = periodoGenerazione;
//	}
	
	
	/*/* parametri messaggi /**/
	
	public static final double
	updatePositionTime = 2, //periodo di aggiornamento posizione veicoli [millisec]
	pingTime = 250, //tempo tra due ping consecutivi [millisec]
	elaborationTime = 20; //tempo di elaborazione per l'invio di un messaggio [millisec]
	
	
	
	/*/* parametri grafici /**/
	
	public static final int
	distanceFromTheEdge = -6; //distanza del veicolo dall'arco (a livello grafico, minore di 0 = lato destro) [pixel]
	
	
	/*/* parametri dei veicoli /**/

	public static double
	velocitaVeicolo = conversione_Km_h(50); //velocità costante dei veicoli [metri/millisec]
	private static double conversione_Km_h(double km_h){
		//converte da km/h a m/millisec
		//1 km/h = 1/(3.6*10^3) m/millisec
		return 0.00027777777*km_h;
	}
	public static void setVelocitaVeicoli(double chilometriOrari) {velocitaVeicolo = conversione_Km_h(chilometriOrari);}
	
	public static final double
	lunghezzaVeicolo = 3, //[metri]
	distanzaDiSicurezza = 2, //distanza minima tra due veicoli consecutivi [metri]
	//distanza tra i centri di 2 veicoli consecutivi [metri]
	distanzaInterveicolo = lunghezzaVeicolo + distanzaDiSicurezza; 

	
	/*/* parametri RSU /**/
	
	public static   double
	raggioRSU = 15, //raggio d'azione dell'RSU [metri]
	sogliaFeromone = 0.5, //Soglia accettazione scelta prossimo arco non congestionato
	sogliaEvaprorazione = 0.5,
	periodoGenerazione = 200;
	
	public static void setPeriodoGenereazione(double rate){
		periodoGenerazione = rate;
	}

	/*/* parametri semafori /**/
	
	public static final Semaforo.Type tipoSemaforo = Semaforo.Type.intelligente;
	public static final Regolatore.Type tipoRegolatore = Regolatore.Type.INTELLIGENTE;

	public static final double
	//il semaforo intelligente concede autorizzazioni se il primo veicolo della coda
	//non supera la distanza minima [metri]
	distanzaMinimaAutorizzazione = 3.0,
	//viene accettata l'autorizzazione al prossimo veicolo se la distanza
	//dall'ultimo veicolo autorizzato è minore della soglia di accettazione
	sogliaDiAccettazione = 1;
	
	
	
	// SETTER //////////////////////////////////////
	
	
	
}
