package ITS.semafori;

import ITS.regolatoriSemafori.Regolatore;
import ITS.regolatoriSemafori.RegolatoreCodaLunga;
import network.CityGraph;
import network.NetEdge;
import util.Param;
import veicoli.Vehicle;

public class SemaforoCodaLunga extends ABS_Semaforo {

	private boolean verde = false;
	private double raggioAccettazione = Param.distanzaMinimaAutorizzazione;
	private double sogliaCongestione = 0.8;
	// soglia minima e massima del tempo di verde
	private double tempoMin, tempoMax;

	public SemaforoCodaLunga(Regolatore regolatore, NetEdge edge) {
		super(regolatore, edge);
		CityGraph g = (CityGraph) edge.getTargetNode().getGraph();
		// lista che viene assegnata come riferimento dal grafo che ne gestisce aggiunta e
		// rimozione
		coda = g.getVehiclesOnTheEdge(edge.getId());

		// tempo per far arrivare all'incrocio l'auto all'interno del raggio
		tempoMin = (raggioAccettazione / Param.velocitaVeicolo) + 1.0;

		// tempo per far passare tutta la coda
		tempoMax = ((double) edge.getAttribute("length") / Param.velocitaVeicolo) - tempoMin;
	}

	// METHODS /////////////////
	private boolean macchineVicine() {
		double lunghezzaArco = ((Double) edge.getAttribute("length"));
		double distanzaMinima = lunghezzaArco - ((Double) coda.getFirst().getPositionOnTheEdge());
		double distanzaVeicolo;
		for (Vehicle v : coda) {
			distanzaVeicolo = lunghezzaArco - ((Double) v.getPositionOnTheEdge());
			if (distanzaMinima > distanzaVeicolo) {
				distanzaMinima = distanzaVeicolo;
			}
		}

		return distanzaMinima < raggioAccettazione;
	}

	// OVERRIDE ////////////////////
	@Override
	public double setVerde() {
		if (coda.isEmpty()) return 0.0;

		if (!macchineVicine()) return 0.0;

		// restituisce quanto contribuisce quest'arco a congestionare l'incrocio
		double gradoCongestione = ((double) ((RegolatoreCodaLunga) regolatore).getCongestione(this));

		// si da preferenza alle code che provocano più congestione
//		System.out.println("semaforo coda lunga "+percentuale);
		double tempoDiVerde = ((double) (gradoCongestione * tempoMax) + tempoMin);

		verde = true;
		avvisaVeicoli("VERDE");

		return tempoDiVerde;

	}

	@Override
	public void setRosso() {
		if (!sempreVerde) {
			verde = false;
			avvisaVeicoli("ROSSO");
		} else {
			setVerde();
		}
	}

	@Override
	public NetEdge getEdge() {
		return edge;
	}

}
