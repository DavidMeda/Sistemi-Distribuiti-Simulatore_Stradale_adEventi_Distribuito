package statistiche;

import java.io.Serializable;
import ITS.RSU.RemoteRSU;

public class VariabileStatRSU implements Serializable {

	private static final long serialVersionUID = 1L;
	private RemoteRSU rsu;
	private int idRSU;

	// variabili RSU
	private double tempoInizio = 0.0, tempoFine = 0.0, gradoCongestione = 0.0;
	private static double durataTotale = 1;
	private int numIndirizzamenti = 0, numIndirizzamentiPercorsoMinimo = 0, messRicevutiRSU_RSU = 0,
					messInviatiRSU_RSU = 0, messRicevutiRSU_Veicolo = 0, messInviatiRSU_Veicolo = 0, messRicevuti = 0,
					messInviati = 0, messTotali = 0, veicoliPerRSU = 0;

	public VariabileStatRSU(RemoteRSU r, int id) {
		rsu = r;
		idRSU = id;
	}

	public void setDurataTotale(double t) {
		durataTotale = t;
	}

	// GETTERS ///////////
	public int getVeicoliPerRSU() {
		return veicoliPerRSU;
	}

	public int getIdRSU() {
		return idRSU;
	}

	public double getGradoCongestione() {
		return gradoCongestione;
	}

	public double getTempoIniziale() {
		return tempoInizio;
	}

	public double getTempoFinale() {
		return tempoFine;
	}

	public double getDurataTotale() {
		return durataTotale;
	}

	public int getNumeroMessRicevutiRSU_RSU() {
		return messRicevutiRSU_RSU;
	}

	public int getNumeroMessInviatiRSU_RSU() {
		return messInviatiRSU_RSU;
	}

	public int getNumeroMessRicevutiRSU_Veicolo() {
		return messRicevutiRSU_Veicolo;
	}

	public int getNumeroMessInviatiRSU_Veicolo() {
		return messInviatiRSU_Veicolo;
	}

	public int getNumeroMessTotali() {
		messTotali = messInviatiRSU_RSU + messInviatiRSU_Veicolo + messRicevutiRSU_RSU + messRicevutiRSU_Veicolo;
		return messTotali;
	}

	public double getNumeroIndirizzamenti() {
		return numIndirizzamenti;
	}

	public double getNumeroIndirizzamentiSuPercorsoMinimo() {
		return numIndirizzamentiPercorsoMinimo;
	}

	public double getGradoOttimalitaPercorsiMin() {
		// System.out.println(this + "percosoMini: " + numeroIndirizzamentiSuPercorsoMinimo +
		// "percorsiTOT: "
		// + numeroIndirizzamenti);
		if (numIndirizzamenti == 0) return 0.0;
		return numIndirizzamentiPercorsoMinimo / numIndirizzamenti;
	}

	// UPDATE////////////////////////////////////////////

	public void updateGradoCongestione() {
		if (veicoliPerRSU == 0) gradoCongestione = 0;
		else gradoCongestione = durataTotale / veicoliPerRSU;
	}

	public void updateDurataTotale() {
		setDurataTotale((tempoFine - tempoInizio) / 1000);
	}

	public void updateTempoFine(double time) {
		tempoFine = time;
		// durataTotale = (tempoFine - tempoInizio) / 1000;
	}

	public void updateTempoInizio(double time) {
		tempoInizio = time;
	}

	public void updateNumeroIndirizzamentiSuPercorsoMinimo() {
		numIndirizzamentiPercorsoMinimo++;
	}

	public void updateNumeroIndirizzamenti() {
		numIndirizzamenti++;
	}

	public void updateMessaggiRicevutiRSU_RSU() {
		messRicevutiRSU_RSU++;
	}

	public void updateMessaggiInviatiRSU_RSU() {
		messInviatiRSU_RSU++;
	}

	public void updateMessaggiRicevutiRSU_Veicolo() {
		messRicevutiRSU_Veicolo++;
	}

	public void updateMessaggiInviatiRSU_Veicolo() {
		messInviatiRSU_Veicolo++;
	}

	public void updateMessaggiRicevuti() {
		messRicevuti++;
	}

	public void updateMessaggiInviati() {
		messInviati++;
	}

	public void updateMessaggiTotali() {
		messTotali++;
	}

	public void updateVeicoliPerRSU() {
		veicoliPerRSU++;
	}

	// METHODS ////////////

	public void totMessaggi() {
		messTotali = messInviatiRSU_RSU + messInviatiRSU_Veicolo + messRicevutiRSU_RSU + messRicevutiRSU_Veicolo;
	}

	public void totMessaggiRicevuti() {
		messRicevuti = messRicevutiRSU_RSU + messRicevutiRSU_Veicolo;
	}

	public void totMessaggiInviati() {
		messInviati = messInviatiRSU_RSU + messInviatiRSU_Veicolo;
	}

}
