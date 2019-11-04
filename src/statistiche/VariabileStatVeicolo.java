package statistiche;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;
import network.NetEdge;
import veicoli.Vehicle;

public class VariabileStatVeicolo implements Serializable, Comparable<VariabileStatVeicolo> {

	private static final long serialVersionUID = 1L;
	private String IDveicolo;
	private double distanzaPercorsa = 0.0, tempoTotalePercorrenza = 0.0, tempoTotaleAttesa = 0.0;
	private int numAttraversamenti = 0, numSemaforiRossi = 0, numSemaforiVerdi = 0;
	private String IDnodoPartenza, IDnodoDestinazione;
	private LinkedList<String> percorso = new LinkedList<String>();

	public VariabileStatVeicolo(String id) {
		IDveicolo = id;
	}

	// UPDATES ////
	public void nodoPartenza(String id) {
		IDnodoPartenza = id;
	}
	
	public void nodoDestinazione(String id) {
		IDnodoDestinazione = id;
	}

	public void updateDistanzaPercorsa(double distanza) {
		distanzaPercorsa = distanza;
	}

	public void updateTempoTot(double tempo) {
		tempoTotalePercorrenza = tempo;
	}

	public void updateTempoTotAttesa(double tempoAttesa) {
		tempoTotaleAttesa = tempoAttesa;
	}

	public void updateNumAttraversamenti() {
		numAttraversamenti++;
	}

	public void updateSemaforiRossi() {

		numSemaforiRossi++;
	}

	public void updateSemaforiVerdi() {
		numSemaforiVerdi++;
	}

	public void aggiungiStrade(String arco) {
		percorso.add(arco);
	}

	/// GETTERS ////
	
	
	public String getIDveicolo() {
		return IDveicolo;
	}

	
	public String getIDnodoPartenza() {
		return IDnodoPartenza;
	}

	
	public String getIDnodoDestinazione() {
		return IDnodoDestinazione;
	}

	public LinkedList<String> getPercorso() {
		return percorso;
	}

	public double getDistanzaPercorsa() {
		return distanzaPercorsa;
	}

	public double getTempoTotalePercorrenza() {
		return tempoTotalePercorrenza / 1000;
	}

	public double getTempoTotaleAttesa() {
		return tempoTotaleAttesa / 1000;
	}

	public int getNumAttraversamenti() {
		return numAttraversamenti;
	}

	public int getNumSemaforiRossi() {
		return numSemaforiRossi;
	}

	public int getNumSemaforiVerdi() {
		return numSemaforiVerdi;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Statistiche veicolo " + IDveicolo);
		sb.append("\n\tpartenza: nodo["+IDnodoPartenza+"]");
		sb.append("\n\tdestinazione: nodo["+IDnodoDestinazione+"]");
		sb.append("\n\tdistanza percorsa [metri]: " + String.format("%4.3f" , distanzaPercorsa));
		sb.append("\n\ttempo Totale percorrenza: " + tempoTotalePercorrenza / 1000);
		sb.append("\n\ttempo totale attesa: " + tempoTotaleAttesa / 1000);
		sb.append("\n\ttempo totale in movimento: " + (tempoTotalePercorrenza - tempoTotaleAttesa) / 1000);
		sb.append("\n\tnumero attraversamenti: " + numAttraversamenti);
		sb.append("\n\tnumero semafori rossi: " + numSemaforiRossi);
		sb.append("\n\tnumero semafori verdi: " + numSemaforiVerdi);
		sb.append("\n\tStrade percorse: " + Arrays.asList(percorso));
		return sb.toString();

	}

	@Override
	public int compareTo(VariabileStatVeicolo o) {
		if (IDveicolo.equals(o.getIDveicolo())) return 0;
		return -1;
	}

}
