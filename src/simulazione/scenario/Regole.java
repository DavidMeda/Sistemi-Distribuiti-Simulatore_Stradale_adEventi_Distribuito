package simulazione.scenario;

import java.util.LinkedList;
import simulazione.Launcher.MainSimulazione;
import veicoli.Vehicle;

public abstract class Regole {
	
	
	public abstract void applicaRegole(MainSimulazione simulazione, LinkedList<Vehicle>listaVeicoli);
	
	
	
	protected void partenzeVeicoli(MainSimulazione simulazione, LinkedList<Vehicle> veicoli){
		Vehicle veicolo = null;
		double periodoDiGenerazione = simulazione.getPeriodoGenerazione();
		
		
		for (int j = 0; j < veicoli.size(); j++) {
			veicolo = veicoli.get(j);
			veicolo.beginsToMoveAt(j*periodoDiGenerazione);
			
			/*print*
			System.out.println(this+": init "+veicolo);
			System.out.println(this+": grafo = "+cityGraph);
			System.out.println(this+": nodo partenza = "+nodoPartenza);
			System.out.println(this+": nodo destinazione = "+nodoDestinazione);



			/**/
		}
	}

}
