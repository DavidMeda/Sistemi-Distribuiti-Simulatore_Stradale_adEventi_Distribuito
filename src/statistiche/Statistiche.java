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
	private LinkedList<RemoteRSU> listaRSU = new LinkedList<RemoteRSU>();
	private HashMap<RemoteRSU, Variabili> mappaStatistiche = new HashMap<RemoteRSU, Variabili>();
	private static final long serialVersionUID = -6219374298605448637L;
	private int countRSU = 0;
	
	private String
	idRSUminimoTempo,
	idRSUmassimoTempo,
	idRSUminimoMessaggi,
	idRSUmassimoMessaggi;
	
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
	public synchronized void updateStatistiche(RemoteRSU RSU, Variabili var) throws RemoteException {
//		System.out.println("Ricevo aggiornamento statistiche da   "+RSU.getNameRSU());
		mappaStatistiche.put(RSU, var);
	}

	public synchronized void richiestaStatisticheGenerali() throws RemoteException {
		countRSU++;
		System.out.println("richieste statostiche: "+countRSU +" e ne mancano "+(listaRSU.size()-countRSU));
		if(countRSU == listaRSU.size()) inviaStatisticheGenerali();
	}
	
	public void inviaStatisticheGenerali() throws RemoteException{
		update();
		 String statistiche = "\n\nSTATISTICHE RSU"
//				+"\nGrado dei tempi medi di attesa = "+getGradoTempoMedioAttesa()
				+"\nMedia messaggi totali per RSU = "+getMediaMessTotali()
				+"\nMedia messaggi ricevuti per RSU = "+getMediaMessRicevuti()
				+"\nMedia messaggi inviati per RSU = "+getMediaMessInviati()
				+"\nRSU col minor tempo medio di attesa = "+getSemaforoConMinimoTempoAttesaMedio()+" (tempo = "+tempoMedioDiAttesaMinimo+" millisec )"
				+"\nRSU col maggior tempo medio di attesa = "+getSemaforoConMassimoTempoAttesaMedio()+" (tempo = "+tempoMedioDiAttesaMassimo+" millisec )"
				+"\nRSU col minor numero di messaggi = "+getRSUminimoNumDiMessaggi()+" (numero = "+numMessMinimo+")"
				+"\nRSU col maggior numero di messaggi = "+getRSUmassimoNumDiMessaggi()+" (numero = "+numMessMassimo+")"
//			  +"\nGrado di scelta ottimale: "+  String.format("%.3f",getGradoSceltaOttimale())
			  +"\nINFO:"
			  +"\n  - Sommatoria messaggi totali = "+getNumeroMessTotali()
			  +"\n  - Sommatoria messaggi ricevuti da RSU = "+getNumeroMessRicevutiRSU_RSU()
			  +"\n  - Sommatoria messaggi ricevuti da Veicoli = "+getNumeroMessRicevutiRSU_Veicoli()
			  +"\n  - Sommatoria messaggi inviati a RSU = "+getNumeroMessInviatiRSU_RSU()
			  +"\n  - Sommatoria messaggi Inviati a Veicoli = "+getNumeroMessInviatiRSU_Veicoli()
			  +"\n";
		 for(RemoteRSU rsu : listaRSU) {
			 rsu.stampaStatistiche(statistiche);
		 }
	}

	public synchronized void registraRSU(RemoteRSU rsu) throws RemoteException {
		System.out.println("si  è aggiunto " + rsu.getNameRSU());
		listaRSU.add(rsu);
	}
	
	
	// GETTER ///////////////////////////
		public double getNumeroMessTotali(){return sommaMessTotali;}
		public double getNumeroMessRicevutiRSU_RSU(){return sommaMessRicevutiRSU_RSU;}
		public double getNumeroMessInviatiRSU_RSU(){return sommaMessInviatiRSU_RSU;}
		public double getNumeroMessInviatiRSU_Veicoli(){return sommaMessInviatiRSU_Veicoli;}
		public double getNumeroMessRicevutiRSU_Veicoli(){return sommaMessRicevutiRSU_Veicoli;}
		public int getMediaMessRicevuti(){return (int) (( sommaMessRicevutiRSU_RSU + sommaMessRicevutiRSU_Veicoli) / mappaStatistiche.size());}
		public int getMediaMessInviati(){return (int) (( sommaMessInviatiRSU_RSU + sommaMessInviatiRSU_Veicoli) / mappaStatistiche.size());}
		public int getMediaMessTotali(){return (int) ((sommaMessTotali)/mappaStatistiche.size());}
		
		public String getRSUminimoNumDiMessaggi(){return idRSUminimoMessaggi;}
		public String getRSUmassimoNumDiMessaggi(){return idRSUmassimoMessaggi;}
		public String getSemaforoConMinimoTempoAttesaMedio() {return idRSUminimoTempo;}
		public String getSemaforoConMassimoTempoAttesaMedio() {return idRSUmassimoTempo;}
		public double getTempoMedioDiAttesaMinimo() {return tempoMedioDiAttesaMinimo;}
		public double getTempoMedioDiAttesaMassimo() {return tempoMedioDiAttesaMassimo;}
		public double getGradoTempoMedioAttesa() {
			double sommaTempiMediAttesa = 0.0;
			for(Entry<RemoteRSU, Variabili> e : mappaStatistiche.entrySet()) {
				sommaTempiMediAttesa += e.getValue().getTempoMedioAttesa();
			}
			return sommaTempiMediAttesa / mappaStatistiche.size();
		}
//		public double getGradoSceltaOttimale(){
//			double sommaGradi = 0.0, cont = 0;
//			for(Entry<RemoteRSU, Variabili> e : mappaStatistiche.entrySet()){
//				//Considero solo gli RSU che hanno fatto indirizzamento
//				if(e.getValue().getNumeroIndirizzamenti() != 0) cont++;
//				sommaGradi += e.getValue().getIndirizzamentoPercorsoMinimo();
//			}
//			return sommaGradi / cont;
//		}
		
		public void update() throws RemoteException {
			updateSemaforoConMassimoTempoAttesaMedio();
			updateSemaforoConMinimoTempoAttesaMedio();
			updateMessaggi();
			updateRSUnumMassimoMessaggi();
			updateRSUnumMinimoMessaggi();
		}
		
		public void updateSemaforoConMinimoTempoAttesaMedio()throws RemoteException {
			Variabili var;
			for(Entry<RemoteRSU, Variabili> e : mappaStatistiche.entrySet()) {
				var = e.getValue();
				if(var.getTempoMedioAttesa() > 0 && var.getTempoMedioAttesa() < tempoMedioDiAttesaMinimo) {
					tempoMedioDiAttesaMinimo = (int) var.getTempoMedioAttesa();
					idRSUminimoTempo = ""+e.getKey().getNameRSU();
				}
			}
			
		}
		
		public void updateSemaforoConMassimoTempoAttesaMedio() throws RemoteException {
			Variabili var;
			for(Entry<RemoteRSU, Variabili> e : mappaStatistiche.entrySet()) {
				var = e.getValue();
				if(var.getTempoMedioAttesa() > tempoMedioDiAttesaMassimo) {
					tempoMedioDiAttesaMassimo = (int) var.getTempoMedioAttesa();
					idRSUmassimoTempo = ""+e.getKey().getNameRSU();
				}
			}
			
			
		}
		
		public void updateMessaggi(){
			Variabili var;

			for(Entry<RemoteRSU, Variabili> e : mappaStatistiche.entrySet()) {
				var = e.getValue();
				sommaMessTotali += var.getNumeroMessTotali();
				sommaMessRicevutiRSU_RSU += var.getNumeroMessRicevutiRSU_RSU();
				sommaMessInviatiRSU_RSU += var.getNumeroMessRicevutiRSU_RSU();
				sommaMessInviatiRSU_Veicoli += var.getNumeroMessInviatiRSU_Veicolo();
				sommaMessRicevutiRSU_Veicoli += var.getNumeroMessRicevutiRSU_Veicolo();
			}
		}
		
		public void updateRSUnumMinimoMessaggi()throws RemoteException {
			Variabili var;

			for(Entry<RemoteRSU, Variabili> e : mappaStatistiche.entrySet()) {
				var = e.getValue();
				if(var.getNumeroMessTotali() > 0 && var.getNumeroMessTotali() < numMessMinimo){
					numMessMinimo = var.getNumeroMessTotali();
					idRSUminimoMessaggi = ""+e.getKey().getNameRSU();
				}
			}
		}
		
		public void updateRSUnumMassimoMessaggi()throws RemoteException {
			Variabili var;

			for(Entry<RemoteRSU, Variabili> e : mappaStatistiche.entrySet()) {
				var = e.getValue();
				if(var.getNumeroMessTotali() > numMessMassimo){
					numMessMassimo = var.getNumeroMessTotali();
					idRSUmassimoMessaggi = ""+e.getKey().getNameRSU();
				}
			}
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
