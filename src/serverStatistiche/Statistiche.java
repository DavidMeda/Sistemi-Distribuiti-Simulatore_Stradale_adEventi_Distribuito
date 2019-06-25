package serverStatistiche;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.LinkedList;
import ITS.RSU.RSU;
import ITS.RSU.RemoteRSU;

public class Statistiche extends UnicastRemoteObject implements ServerStatistiche {

	// private HashMap<String, Double> mappaStatistiche = new HashMap<String, Double>();
	private LinkedList<RemoteRSU> listaRSU = new LinkedList<RemoteRSU>();
	private HashMap<RSU, HashMap<String, Double>> mappaStatistiche = new HashMap<RSU, HashMap<String, Double>>();
	double numMessaggi, numAuto;

	public Statistiche() throws RemoteException {
		super();
	}

	private static final long serialVersionUID = -6219374298605448637L;

	@Override
	public void updateStatistiche(String nomeVariabile, double quantitaMes, double quantitaAuto ) throws RemoteException {
		numMessaggi += quantitaMes;
		numAuto +=quantitaAuto;
		System.out.println("HO ricevuto le statistiche da RSU\nmes "+numMessaggi+" numAu "+numAuto);
		for(RemoteRSU r : listaRSU) {
			r.stampaStatistiche(numAuto);
			r.stampaStatistiche(quantitaMes);
		}
//		if (mappaStatistiche.containsKey(rsu)) {
//			HashMap<K, V>
//			if (mappaStatistiche.containsKey(nomeVariabile)) {
//				double tempValue = mappaStatistiche.get(nomeVariabile) + quantita;
//				mappaStatistiche.put(nomeVariabile, tempValue);
//			}
//
//		} else {
//			mappaStatistiche.put(nomeVariabile, quantita);
//		}
	}

	public void registraRSU(RemoteRSU rsu) {
		System.out.println("si  è aggiunto "+rsu);
		listaRSU.add( rsu);
	}
	
	public static void main(String[] args) {
		try {
			Statistiche stat = new Statistiche();
			LocateRegistry.createRegistry(1099);
			System.out.println("Creato registro");
			Naming.rebind("Server",stat);
			System.out.println("fatto lookup");
		}catch (Exception e) {e.printStackTrace();
		}
	}

}
