package simulazioni.regole;

import java.util.LinkedList;

import simulazioni.Simulazione;
import vanet.Vehicle;

public abstract class Regole {
	
	
	public abstract void applicaRegole(Simulazione simulazione, LinkedList<Vehicle>listaVeicoli);
	
	
	
	protected void partenzeVeicoli(Simulazione simulazione, LinkedList<Vehicle> veicoli){
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
