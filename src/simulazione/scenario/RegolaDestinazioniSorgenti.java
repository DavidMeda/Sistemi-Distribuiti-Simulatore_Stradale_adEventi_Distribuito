package simulazione.scenario;

import java.util.ArrayList;
import java.util.LinkedList;
import network.CityGraph;
import simulazione.Launcher.MainSimulazione;
import util.Param;
import veicoli.Vehicle;

public class RegolaDestinazioniSorgenti extends Regole {
	/**
	 * Questa regola serve per settare quante sorgenti e quante destinazioni possono essere raggiunte dai veicoli
	 * 
	 * es: se numSorgenti=3 si scelgono 3 sorgenti a caso dal grafo e una di queste tre viene assegnata ai veicoli
	 * e numDestinazioni=0 viene scelta random dal grafo
	 *    
	 * 
	 */
	
	private int numeroDestinazioni = 0, numeroSorgenti = 0;
	
	
	
	public RegolaDestinazioniSorgenti(int numDestinazioni, int numSorgenti) {
		numeroDestinazioni = numDestinazioni;
		numeroSorgenti = numSorgenti;
	}
	
	
	

	@Override
	public void applicaRegole(MainSimulazione simulazione, LinkedList<Vehicle> listaVeicoli) {
		partenzeVeicoli(simulazione, listaVeicoli);
		CityGraph grafo = simulazione.getGrafo();
		
//		System.out.println(this+": numero sorgenti = "+numeroSorgenti+" - numero destinazioni = "+numeroDestinazioni);
		
		ArrayList<Integer> destinazioni = new ArrayList<Integer>(numeroDestinazioni);
		
		int idNodoDestinazione = 0;
		for(int i=0; i<numeroDestinazioni; i++){
			do{
				idNodoDestinazione = Param.random.nextInt(grafo.getNodeCount());
				
				
			}while(destinazioni.contains(idNodoDestinazione));
			
			/*print*
			System.out.println(this+": aggiunto "+idNodoDestinazione+" alle destinazioni");
			/**/
			destinazioni.add(idNodoDestinazione);
		
		}
		
		/*print*
		System.out.println(this+": destinazioni scelte = "+destinazioni);
		/**/
		
		ArrayList<Integer> sorgenti = new ArrayList<Integer>(numeroSorgenti);
		int idNodoSorgente = 0;
		for(int i=0; i<numeroSorgenti; i++){
			do{
				idNodoSorgente = Param.random.nextInt(grafo.getNodeCount());
				
				
			}while(destinazioni.contains(idNodoSorgente) || sorgenti.contains(idNodoSorgente));
			sorgenti.add(idNodoSorgente);
		
		}
		/*print*
		System.out.println(this+": sorgenti scelte = "+sorgenti);
		/**/
		
		int sorgente = 0, destinazione = 0;
		for(Vehicle auto : listaVeicoli){
			if(numeroDestinazioni == 0 && numeroSorgenti == 0){
				destinazione = Param.random.nextInt(grafo.getNodeCount());
				do{
					sorgente = Param.random.nextInt(grafo.getNodeCount());

				}while(destinazione== sorgente);
				
				auto.setStartingNode(sorgente);
				auto.setTargetNode(destinazione);
				continue;

			}
			//destinazione random del grafo
			if(numeroDestinazioni == 0){
				
				do{
					destinazione = Param.random.nextInt(grafo.getNodeCount());
				}while(sorgenti.contains(destinazione));
				
				auto.setTargetNode(destinazione);
				/*print*
				System.out.println(this+": destinazioni infinite -- "+auto+" destinazione "+auto.getTargetNode());
				/**/
			}
			//destinazione random dalla lista destinazioni
			else{
				destinazione = destinazioni.get(Param.random.nextInt(numeroDestinazioni));
				auto.setTargetNode(destinazione);
				/*print*
				System.out.println(this+": "+auto+" destinazione "+auto.getTargetNode());
				/**/
			}
			
			//sorgenti random dal  grafo
			if(numeroSorgenti == 0){
				
				do{
					sorgente = Param.random.nextInt(grafo.getNodeCount());
				}while(destinazioni.contains(sorgente));
				
				auto.setStartingNode(sorgente);
				/*print*
				System.out.println(this+": sorgenti infinite -- "+auto+" sorgente "+auto.getStartingNode());
				/**/
			}
			
			//sorgente random tra le lista Sorgenti
			else{
				sorgente = sorgenti.get(Param.random.nextInt(numeroSorgenti));
				auto.setStartingNode(sorgente);
				/*print*
				System.out.println(this+": "+auto+" sorgente "+auto.getStartingNode());
				/**/
			}
			
			
		}
		
	}

}
