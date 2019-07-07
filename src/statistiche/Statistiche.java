package statistiche;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map.Entry;
import ITS.RSU.RemoteRSU;

public class Statistiche extends UnicastRemoteObject implements ServerStatistiche {

	private HashMap<RemoteRSU, Variabile> mappaStatistiche = new HashMap<RemoteRSU, Variabile>();
	private static final long serialVersionUID = -6219374298605448637L;
	private int countRSU = 0;
	private boolean nuovaSesione = true;

	private String idRSUminimoMessaggi, idRSUmassimoMessaggi;

	private int 
	tempoMedioDiAttesaMinimo = Integer.MAX_VALUE, 
	tempoMedioDiAttesaMassimo = Integer.MIN_VALUE,
	numMessMassimo = Integer.MIN_VALUE, 
	numMessMinimo = Integer.MAX_VALUE,
	sommaMessTotali, 
	sommaMessInviatiRSU_RSU, 
	sommaMessRicevutiRSU_RSU, 
	sommaMessInviatiRSU_Veicoli,
	sommaMessRicevutiRSU_Veicoli;

	public Statistiche() throws RemoteException {
		super();
	}

	@Override
	public synchronized void updateStatistiche(RemoteRSU RSU, Variabile var) throws RemoteException {
		// System.out.println("Ricevo aggiornamento statistiche da "+RSU.getNameRSU());
		mappaStatistiche.put(RSU, var);
	}

	public synchronized void richiestaStatisticheGenerali() throws RemoteException {
		countRSU++;
		System.out.println("richiesta n: " + countRSU + " mancano " + ((mappaStatistiche.size()) - countRSU));
		if (countRSU >= (mappaStatistiche.size() )) {
			inviaStatisticheGenerali();
			nuovaSesione = true;
			System.out.println("LA PROSSIMA VOLTA CANCELLO");
		}
	}
	
	public synchronized void registraRSU(RemoteRSU rsu) throws RemoteException {
		if (nuovaSesione) {
			reset();
			nuovaSesione = false;
			System.out.println("CANCELLO MAPPA VECCHIA");
		}
		mappaStatistiche.put(rsu, null);
		System.out.println("si  è aggiunto " + rsu.getNameRSU() + " size= " + mappaStatistiche.size());
	}

	public void inviaStatisticheGenerali() throws RemoteException {
		update();
		String statistiche = "\n\nSTATISTICHE RSU" + "\nMedia messaggi totali per RSU = " + getMediaMessTotali()
						+ "\nMedia messaggi ricevuti per RSU = " + getMediaMessRicevuti()
						+ "\nMedia messaggi inviati per RSU = " + getMediaMessInviati()
						+ "\nRSU col minor numero di messaggi = " + getRSUminimoNumDiMessaggi() + " (numero = "
						+ numMessMinimo + ")" + "\nRSU col maggior numero di messaggi = " + getRSUmassimoNumDiMessaggi()
						+ " (numero = " + numMessMassimo + ")" + "\nINFO:" + "\n  - Sommatoria messaggi totali = "
						+ getNumeroMessTotali() + "\n  - Sommatoria messaggi ricevuti da RSU = "
						+ getNumeroMessRicevutiRSU_RSU() + "\n  - Sommatoria messaggi ricevuti da Veicoli = "
						+ getNumeroMessRicevutiRSU_Veicoli() + "\n  - Sommatoria messaggi inviati a RSU = "
						+ getNumeroMessInviatiRSU_RSU() + "\n  - Sommatoria messaggi Inviati a Veicoli = "
						+ getNumeroMessInviatiRSU_Veicoli() + "\n";
		for (Entry<RemoteRSU, Variabile> e : mappaStatistiche.entrySet()) {
			e.getKey().stampaStatistiche(statistiche);
		}
	}
	
	private void reset() {
		countRSU = 0;
		
		mappaStatistiche.clear();
		mappaStatistiche = new HashMap<RemoteRSU, Variabile>();
		tempoMedioDiAttesaMinimo = Integer.MAX_VALUE; 
		tempoMedioDiAttesaMassimo = Integer.MIN_VALUE;
		numMessMassimo = Integer.MIN_VALUE; 
		numMessMinimo = Integer.MAX_VALUE;
		sommaMessTotali = 0; 
		sommaMessInviatiRSU_RSU= 0;  
		sommaMessRicevutiRSU_RSU= 0;  
		sommaMessInviatiRSU_Veicoli= 0; 
		sommaMessRicevutiRSU_Veicoli= 0;
		idRSUminimoMessaggi = ""; 
		idRSUmassimoMessaggi = "";
	}

	

	// GETTER ///////////////////////////
	public double getNumeroMessTotali() {
		return sommaMessTotali;
	}

	public double getNumeroMessRicevutiRSU_RSU() {
		return sommaMessRicevutiRSU_RSU;
	}

	public double getNumeroMessInviatiRSU_RSU() {
		return sommaMessInviatiRSU_RSU;
	}

	public double getNumeroMessInviatiRSU_Veicoli() {
		return sommaMessInviatiRSU_Veicoli;
	}

	public double getNumeroMessRicevutiRSU_Veicoli() {
		return sommaMessRicevutiRSU_Veicoli;
	}

	public int getMediaMessRicevuti() {
		return (int) ((sommaMessRicevutiRSU_RSU + sommaMessRicevutiRSU_Veicoli) / mappaStatistiche.size());
	}

	public int getMediaMessInviati() {
		return (int) ((sommaMessInviatiRSU_RSU + sommaMessInviatiRSU_Veicoli) / mappaStatistiche.size());
	}

	public int getMediaMessTotali() {
		return (int) ((sommaMessTotali) / mappaStatistiche.size());
	}

	public String getRSUminimoNumDiMessaggi() {
		return idRSUminimoMessaggi;
	}

	public String getRSUmassimoNumDiMessaggi() {
		return idRSUmassimoMessaggi;
	}

	public double getTempoMedioDiAttesaMinimo() {
		return tempoMedioDiAttesaMinimo;
	}

	public double getTempoMedioDiAttesaMassimo() {
		return tempoMedioDiAttesaMassimo;
	}

	public double getGradoTempoMedioAttesa() {
		double sommaTempiMediAttesa = 0.0;
		for (Entry<RemoteRSU, Variabile> e : mappaStatistiche.entrySet()) {
			sommaTempiMediAttesa += e.getValue().getTempoMedioAttesa();
		}
		return sommaTempiMediAttesa / mappaStatistiche.size();
	}

	public void update() throws RemoteException {
		updateMessaggi();
		updateRSUnumMassimoMessaggi();
		updateRSUnumMinimoMessaggi();
	}

	public void updateMessaggi() {
		Variabile var;

		for (Entry<RemoteRSU, Variabile> e : mappaStatistiche.entrySet()) {
			var = e.getValue();
			sommaMessTotali += var.getNumeroMessTotali();
			sommaMessRicevutiRSU_RSU += var.getNumeroMessRicevutiRSU_RSU();
			sommaMessInviatiRSU_RSU += var.getNumeroMessRicevutiRSU_RSU();
			sommaMessInviatiRSU_Veicoli += var.getNumeroMessInviatiRSU_Veicolo();
			sommaMessRicevutiRSU_Veicoli += var.getNumeroMessRicevutiRSU_Veicolo();
		}
	}

	public void updateRSUnumMinimoMessaggi() throws RemoteException {
		Variabile var;

		for (Entry<RemoteRSU, Variabile> e : mappaStatistiche.entrySet()) {
			var = e.getValue();
			if (var.getNumeroMessTotali() > 0 && var.getNumeroMessTotali() < numMessMinimo) {
				numMessMinimo = var.getNumeroMessTotali();
				idRSUminimoMessaggi = "" + e.getKey().getNameRSU();
			}
		}
	}

	public void updateRSUnumMassimoMessaggi() throws RemoteException {
		Variabile var;

		for (Entry<RemoteRSU, Variabile> e : mappaStatistiche.entrySet()) {
			var = e.getValue();
			if (var.getNumeroMessTotali() > numMessMassimo) {
				numMessMassimo = var.getNumeroMessTotali();
				idRSUmassimoMessaggi = "" + e.getKey().getNameRSU();
			}
		}
	}

	public static void main(String[] args) {
		try {
			Statistiche stat = new Statistiche();
			LocateRegistry.createRegistry(1099);
			System.out.println("Creato SERVER STATISTICHE sul registro Locale");
			Naming.rebind("Server", stat);
			System.out.println("Attendo clienti...\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
