package simulazione.scenario;

import simulazione.scenario.RegolaDestinazioniSorgenti;
import simulazione.scenario.Regole;

public enum Scenario {
	UNA_SORGENTE(new RegolaDestinazioniSorgenti(0, 1)), 
	DUE_SORGENTI(new RegolaDestinazioniSorgenti(0, 2)),

	UNA_DESTINAZIONE(new RegolaDestinazioniSorgenti(1, 0)), 
	DUE_DESTINAZIONI(new RegolaDestinazioniSorgenti(2, 0)),

	UNA_DESTINAZIONE_UNA_SORGENTE(new RegolaDestinazioniSorgenti(1, 1)),
	UNA_DESTINAZIONE_DUE_SORGENTI(new RegolaDestinazioniSorgenti(1, 2)),

	DUE_DESTINAZIONI_UNA_SORGENTE(new RegolaDestinazioniSorgenti(2, 1)),
	DUE_DESTINAZIONI_DUE_SORGENTI(new RegolaDestinazioniSorgenti(2, 2)),

	RANDOM(new RegolaDestinazioniSorgenti(0, 0));

	private Regole regole;

	private Scenario(Regole regole) {
		this.regole = regole;
	}

	public Regole getRegole() {
		return regole;
	}

}
