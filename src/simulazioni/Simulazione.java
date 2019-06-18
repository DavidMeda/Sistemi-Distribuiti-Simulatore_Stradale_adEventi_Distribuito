package simulazioni;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import simulazioni.regole.Regole;
import util.Param;
import vanet.CityGraph;
import vanet.CityGraphXMLParser;
import vanet.Macchina;
import vanet.Vehicle;

public class Simulazione {
	
	private CityGraph grafo;
	private double numeroVeicoli = 0.0;
	private double periodoGenerazione = 0.0;
	private Scenario scenario;
	private String nome;
	
	
	// COSTR //////////////////////////////
	
	public Simulazione(String directoryXMLGraph, double numeroVeicoli, double periodoDiGenerazione, Scenario scenario) {
		super();
		this.grafo = CityGraphXMLParser.getGraph("Cosenza", directoryXMLGraph);
		this.numeroVeicoli = numeroVeicoli;
		this.periodoGenerazione = periodoDiGenerazione;
		this.scenario = scenario;
		this.nome = "seed["+Param.seed+"]";
	}

	
	// GETTER ///////////////////////////////
	
	public CityGraph getGrafo() {return grafo;}
	public double getNumeroVeicoli() {return numeroVeicoli;}
	public String getNome() {return nome;}
	public double getPeriodoGenerazione() {return periodoGenerazione;}
//	public Statistiche getStatistiche() {return grafo.getStatistiche();}


	// METHODS ///////////////////////////////
	
	public void display() {grafo.display().disableAutoLayout();}
	
//	public void initStatistiche() {
		//la creazione delle statistiche del veicolo viene fatta nel veicolo stesso
//		StatisticheVeicoli statisticheVeicoli = new StatisticheVeicoli();
//		StatisticheRSU statisticheRSU = new StatisticheRSU();
		
//		grafo.setStatistiche(new Statistiche());
		
//		RSU rsu = null;
//		for(Node n : grafo.getEachNode()) {
//			rsu = (RSU)n;
//			statisticheRSU.addStatRSU(rsu.getStat());
//		}
//		
//		grafo.addStatistica(statisticheVeicoli);
//		grafo.addStatistica(statisticheRSU);
		
//	}
	
	public void start() throws InterruptedException{
		Param.resetRandom();
		LinkedList<Vehicle> listaVeicoli = new LinkedList<>();
		Vehicle v = null;
		for(int i=0; i<numeroVeicoli; i++){
			v = (Vehicle)grafo.addMobileNode(i, Macchina.class);
			listaVeicoli.add(v);
			v.setAttribute("ui.label", v.getId());

		}
		Regole regole = scenario.getRegole();
		regole.applicaRegole(this, listaVeicoli);
//		initStatistiche();
		grafo.startSimulation();
		
		
	}
	
	
	public static void main(String[] args) throws IOException {
		String dir = new File(System.getProperty("user.dir")+File.separator+"XMLConfig\\CityGraph4.xml").toString();

		Param.setPeriodoGenereazione(200);
//		Param.setPeriodoGenereazione(150);
//		Param.setPeriodoGenereazione(100);
//		Param.setPeriodoGenereazione(50);
		//Setta il tuo seed da qua
		Param.setSeed(389);
		
		
		int numeroVeicoli = 10;
		String feromone = "No Feromone";
		
//		for(int i = 10; i<=numeroMaxVeicoli; i+=100){
			
//			System.out.println("\nSIMULAZIONE "+(i/100)+"\n");
//			System.out.println("Orario INIZIO: "+new OraCorrente()+"\n");
//			System.out.println("veicoli: "+i+" rate = "+Param.periodoGenerazione+"ms "+feromone+" seed["+Param.seed+"]\n");
			Simulazione sim = new Simulazione(dir, numeroVeicoli, Param.periodoGenerazione, Scenario.RANDOM	);
	
			try {
				sim.display();
				sim.start();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
//			System.out.println(sim.getStatistiche());
			
//			System.out.println("\nOrario FINE simulazioni: "+(i/100)+" "+new OraCorrente());
			
//			StatisticheI_O.salvaStatistiche(sim.getStatistiche(), "statistiche con arco N-H semaforo classico", feromone,
//					"periodoGenerazione = "+Param.periodoGenerazione+"ms", i+" auto");
			
			
//		}
//		System.out.println("\n||||||||||||||||||||||||||||||||||||||||||||||||||||||||");
//		System.out.println("FINITOOOOOO!!!");

	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
