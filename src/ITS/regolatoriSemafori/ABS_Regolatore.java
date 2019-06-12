package ITS.regolatoriSemafori;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import ITS.RSU.RSU;
import ITS.semafori.MessageManager;
import ITS.semafori.Semaforo;
import network.NetEdge;
import network.NetNode;
import network.message.Message;
import util.Param;

public abstract class ABS_Regolatore implements Regolatore{
	//tipo di semaforo da utilizzare
	public static Semaforo.Type tipoSemaforo = Param.tipoSemaforo;
		
	//rsu a cui è assegnato il regolatore
	protected RSU sourceRSU;
	
	//fase corrente
	private Fase faseCorrente;
		
	//lista dei semafori (1 per ogni corsia) col nodo di provenienza dell'arco
	protected HashMap<NetNode, ArrayList<Semaforo>> semafori = new HashMap<>(); 
		
	//gestore dei messaggi
	private MessageManager semaphoreMessageManager;	
	
	//variabili per la gestione del round robin delle fasi
	private Iterator<Fase> it;
	private ArrayList<Fase> fasi;
	
	//booleana per assicurarci di  aver chiamato super.init alle sottoclassi
	private boolean init = false;
	
	
	// COSTR /////////////////////
	
	public ABS_Regolatore(RSU rsu, List<NetEdge> archiEntranti) {
		sourceRSU = rsu;
		
		//preparo le liste dei semafori per gli archi entranti
		ArrayList<Semaforo> semaforiPerCorsia = null;
		Semaforo s = null;
		
		//creo la lista per ogni arco corrente
		for(NetEdge edge : archiEntranti){	
			//aggiungo un semaforo per ogni corsia
			int numeroCorsie = 1;
			//int numeroCorsie = strada.getNumCorsie(); TODO
			semaforiPerCorsia = new ArrayList<>(numeroCorsie);
			
			for (int i = 0; i < numeroCorsie; i++) {
				s = Semaforo.getType(tipoSemaforo, this, edge);
				semaforiPerCorsia.add(s);

			}
			
			semafori.put(edge.getSourceNode(), semaforiPerCorsia);
		}
		
		//chiedo il message manager al semaforo
		semaphoreMessageManager = s.getMessageManager();

	}
	
	// SETTER ////////////////////
	public void setFasi(ArrayList<Fase> fasi){
		this.fasi = fasi;
	}
	
	// OVERRIDE ///////////////////
	//from regolatore
	@Override
	public void readMessage(Message message) {
		if(message.getName().equals("CAMBIO FASE")){
			nextPhase();
			


//			//aggiornamento statistiche sulla congestione ad ogni cambio di fase
//			StatRSU stat = source.getStat();
//			stat.updateCongestioneMedia();
//			stat.updateCongestioneMediaArchi();
		
		}else if(semaphoreMessageManager != null){
			semaphoreMessageManager.readMessage(message);
		}
		
	}

	@Override
	public RSU getRSU() {
		return sourceRSU;
	}
	
	@Override
	public Fase nextPhase() {
		
		if(!init) throw new IllegalStateException("Ricorda che devi chiamare super.init di :"+this);
		
		if(!it.hasNext()){ // reset Round Robin
			it = fasi.iterator();
		}
		//disattiva la fase corrente (rosso ai semafori)
		faseCorrente.disattiva();

		//passa alla prossima e attiva (verde ai semafori)
		faseCorrente = it.next();
		faseCorrente.attiva();
		
		//messaggio di cambio fase alla fine del tempo di verde
		double tempoDiFase = faseCorrente.getDurata();
		
		
		//se il verde dura 0 sec il cambio fase avviene dopo un certo tempo
		//serve per evitare loop infiniti sullo scheduler degli eventi
		if(tempoDiFase < Param.elaborationTime) tempoDiFase = Param.elaborationTime;
		
		Message cambioFase = new Message("CAMBIO FASE", sourceRSU, sourceRSU, tempoDiFase);
		sourceRSU.sendEvent(cambioFase);
		
		
		return faseCorrente;
	}
	
	@Override
	public void init(){
		it = fasi.iterator();
		faseCorrente = it.next();
		init = true;

	}
	//from obj
	@Override
	public String toString(){ return "Regolatore-"+sourceRSU;}
	
	/////////////////////////////////////
	// CLASS FASE ///////////////////////
	/////////////////////////////////////
	public static class Fase {
		/* 
		Le fasi hanno un semaforo principale che decide quanto tempo durerà il verde.
		Le sottofasi indicano i semafori che possono essere
		attivati parallelamente al semaforo principale 
		/**/
		private Semaforo semaforoPrincipale;
		private LinkedList<Semaforo> sottoFasi;
		private double durata = 0;
		
		public Fase(Semaforo semafori) {semaforoPrincipale = semafori;}
		
		public void attiva(){
			//attiva il semaforo principale
			durata = semaforoPrincipale.setVerde();
			//attiva le sottofasi
			if(sottoFasi != null){
				if(sottoFasi.size()>0){
					for(Semaforo s : sottoFasi)s.setVerde();				
				}
			}			
		}
		public void disattiva(){
			//disattiva il semaforo principale
			semaforoPrincipale.setRosso();
			durata = 0;
			//disattiva le sottofasi
			if(sottoFasi != null){
				if(sottoFasi.size()>0){
					for(Semaforo s : sottoFasi)s.setRosso();				
				}
			}
			
		}
		public double getDurata(){
			return durata;
		
		}
		public void setSottofasi(LinkedList<Semaforo> sottoFasi){
			this.sottoFasi = sottoFasi;
		}
		
		@Override
		public String toString(){
			return "{"+semaforoPrincipale+" -+- "+sottoFasi+"}";
		}
		
	}
	

}
