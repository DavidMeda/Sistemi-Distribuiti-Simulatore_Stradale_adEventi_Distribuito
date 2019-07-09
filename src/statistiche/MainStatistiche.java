package statistiche;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class MainStatistiche {
	
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
