package statistiche;

import java.io.Serializable;
import ITS.RSU.RemoteRSU;

public class Variabili implements Serializable {
	private static final long serialVersionUID = 1L;
	private RemoteRSU rsu;
	private int idRSU;
	
	//variabili RSU
	
	
	private double 
	sommaTempiAttesa = 0.0,
	numeroVeicoli = 0.0;
	
	private int
	numeroIndirizzamenti = 0,
	numeroIndirizzamentiSuPercorsoMinimo = 0,
	messRicevutiRSU_RSU = 0,
	messInviatiRSU_RSU = 0,
	messRicevutiRSU_Veicolo = 0,
	messInviatiRSU_Veicolo = 0,
	messRicevuti = 0,
	messInviati = 0,
	messTotali = 0,
	veicoliperRSU = 0,
	veicoliTot = 0;
	
	
	public Variabili(RemoteRSU r, int id) {
		rsu = r;
		idRSU = id;
	}
	
	// GETTERS ///////////
	
	public int getIdRSU() {return idRSU;}
	public double getSommaTempiAttesa() {return sommaTempiAttesa;}
	public double getNumeroDiAttraversamenti() {return numeroVeicoli;}
	public double getTempoMedioAttesa() {return sommaTempiAttesa/numeroVeicoli;}
	public int getNumeroMessRicevutiRSU_RSU(){return messRicevutiRSU_RSU;}
	public int getNumeroMessInviatiRSU_RSU(){return messInviatiRSU_RSU;}
	public int getNumeroMessRicevutiRSU_Veicolo(){return messRicevutiRSU_Veicolo;}
	public int getNumeroMessInviatiRSU_Veicolo(){return messInviatiRSU_Veicolo;}
	public int getNumeroMessTotali(){return messTotali;}
	

	
	public double getNumeroIndirizzamenti(){return numeroIndirizzamenti;}
	public double getNumeroIndirizzamentiSuPercorsoMinimo(){return numeroIndirizzamentiSuPercorsoMinimo;}
	public double getIndirizzamentoPercorsoMinimo(){
//		System.out.println(this+"percosoMini: "+numeroIndirizzamentiSuPercorsoMinimo+" percorsiTOT: "+numeroIndirizzamenti);
		if(numeroIndirizzamenti == 0) return 0.0;
		return numeroIndirizzamentiSuPercorsoMinimo / numeroIndirizzamenti;}
	
	
	
	//UPDATE////////////////////////////////////////////7
	
	public void updateNumeroIndirizzamentiSuPercorsoMinimo(){ numeroIndirizzamentiSuPercorsoMinimo++;}
	public void updateNumeroIndirizzamenti(){numeroIndirizzamenti++;}
	
	public void updateMessaggiRicevutiRSU_RSU(){messRicevutiRSU_RSU++;}
	public void updateMessaggiInviatiRSU_RSU(){messInviatiRSU_RSU++;}
	
	public void updateMessaggiRicevutiRSU_Veicolo(){messRicevutiRSU_Veicolo++;}
	public void updateMessaggiInviatiRSU_Veicolo(){messInviatiRSU_Veicolo++;}
	
	public void updateMessaggiRicevuti(){messRicevuti++;}
	public void updateMessaggiInviati(){messInviati++;}
	
	
	public void updateMessaggiTotali(){messTotali++;}
	public void updateVeicoliRSU() {veicoliperRSU++;}
	public void numVeicoliTot(int num) {veicoliTot = num;}
	
	
	// METHODS ////////////
	
	public void aggiornaTempoMedioDiAttesa(double tempoAttesaVeicoloSuArco) {
		//viene aggiornato quando il veicolo cambia arco, quindi in Colony
		sommaTempiAttesa += tempoAttesaVeicoloSuArco;
		numeroVeicoli++;
	
	}
	public void totMessaggi() {
		messTotali = messInviatiRSU_RSU+messInviatiRSU_Veicolo+messRicevutiRSU_RSU+messRicevutiRSU_Veicolo;
	}
	
	public void totMessaggiRicevuti() {
		messRicevuti = messRicevutiRSU_RSU+messRicevutiRSU_Veicolo;
	}
	
	public void totMessaggiInviati() {
		messInviati = messInviatiRSU_RSU+messInviatiRSU_Veicolo;
	}
	
	
	

}
