package statistiche.Launcher;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import simulazione.Launcher.MainSimulazione;
import statistiche.Statistiche;

public class MainStatistiche {

	public static void main(String[] args) {
		try {
			Statistiche stat = new Statistiche();
			LocateRegistry.createRegistry(1099);
			System.out.println("Creato SERVER STATISTICHE sul registro Locale porta 1099");
			Naming.rebind("rmi//localhost:1099/ServerStatistiche", stat);
			System.out.println("Attendo client RSU...\n");

		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
