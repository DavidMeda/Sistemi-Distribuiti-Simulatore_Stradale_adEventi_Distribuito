package simulazioni;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import network.CityGraph;
import network.CityGraphXMLParser;
import simulazioni.regole.Regole;
import util.Param;
import vanet.Macchina;
import vanet.Vehicle;

public class Simulazione  extends JPanel{
	
	private CityGraph grafo;
	private double numeroVeicoli = 0.0;
	private double periodoGenerazione = 0.0;
	private Scenario scenario;
	private String nome;
	
	
	public Simulazione() {}
	
	// GETTER ///////////////////////////////
	
	public CityGraph getGrafo() {return grafo;}
	public double getNumeroVeicoli() {return numeroVeicoli;}
	public String getNome() {return nome;}
	public double getPeriodoGenerazione() {return periodoGenerazione;}


	// METHODS ///////////////////////////////
	
	
	
	public void start(String directoryXMLGraph, double numeroVeicoli, double periodoDiGenerazione, Scenario scenario) throws InterruptedException{
		this.grafo = CityGraphXMLParser.getGraph("Cosenza", directoryXMLGraph);
		grafo.display().disableAutoLayout();
		this.numeroVeicoli = numeroVeicoli;
		this.periodoGenerazione = periodoDiGenerazione;
		this.scenario = scenario;
		this.nome = "seed["+Param.seed+"]";
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
		grafo.startSimulation();
	}
	
	public File chooseFile(){
		JFileChooser fc = new JFileChooser();
		
		fc.setFileFilter(new FileFilter() {
		
			@Override
			public String getDescription() {
				return ".xml";
			}
			
			@Override
			public boolean accept(File file) {
				 if (file.isDirectory()) return true;
				    String fname = file.getName().toLowerCase();
				    return fname.endsWith(".xml");
			}
		});

		fc.setCurrentDirectory(new File(System.getProperty("user.dir")+File.separator+"XMLConfig"));
		int n = fc.showOpenDialog(this);
		
		if(n == JFileChooser.APPROVE_OPTION)return fc.getSelectedFile();
		return null;
	
	}
	
	
	public static void main(String[] args) throws IOException {
//		String dir = new File(System.getProperty("user.dir")+File.separator+"XMLConfig\\CityGraph4.xml").toString();
		Simulazione sim = new Simulazione();
		String directory = sim.chooseFile().toString();
		int numeroVeicoli = 10;
	
		try {
			sim.start(directory, numeroVeicoli, Param.periodoGenerazione, Scenario.RANDOM	);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
			
//			System.out.println("\nOrario FINE simulazioni: "+(i/100)+" "+new OraCorrente());
			
		
		System.out.println("SIMULAZIONE FINITOOOOOO!!!");

	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
