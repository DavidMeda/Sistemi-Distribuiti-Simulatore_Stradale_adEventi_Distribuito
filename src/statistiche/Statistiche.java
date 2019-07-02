package statistiche;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import ITS.RSU.RemoteRSU;

public class Statistiche extends UnicastRemoteObject implements ServerStatistiche {

	// private HashMap<String, Double> mappaStatistiche = new HashMap<String, Double>();
	private LinkedList<RemoteRSU> listaRSU = new LinkedList<RemoteRSU>();
	private HashMap<RemoteRSU, Variabili> mappaStatistiche = new HashMap<RemoteRSU, Variabili>();
	double numTotMessaggi, numTotAuto;

	public Statistiche() throws RemoteException {
		super();
	}

	private static final long serialVersionUID = -6219374298605448637L;

	@Override
	public void updateStatistiche(RemoteRSU RSU, Variabili var) throws RemoteException {
//		System.out.println("Ricevo aggiornamento statistiche da   "+RSU.getNameRSU());
		mappaStatistiche.put(RSU, var);
	}

	public String statisticheGenerali() {
		
		
		return "Statistiche generali";
	}

	public void registraRSU(RemoteRSU rsu) throws RemoteException {
		System.out.println("si  è aggiunto " + rsu.getNameRSU());
		listaRSU.add(rsu);
	}

	public static void main(String[] args) {
		try {
			Statistiche stat = new Statistiche();
			LocateRegistry.createRegistry(1099);
			System.out.println("Creato registro");
			Naming.rebind("Server", stat);
			System.out.println("fatto lookup");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
