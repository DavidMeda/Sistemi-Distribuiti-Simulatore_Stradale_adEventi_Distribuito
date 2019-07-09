package simulazione.Launcher;

import java.awt.Color;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import network.CityGraph;
import network.CityGraphXMLParser;
import simulazione.scenario.Regole;
import simulazione.scenario.Scenario;
import util.Param;
import veicoli.Macchina;
import veicoli.Vehicle;

public class MainSimulazione extends JFrame {
	private static final long serialVersionUID = 1L;
	private CityGraph grafo;
	private double numeroVeicoli = 0.0;
	private double periodoGenerazione = 0.0;
	private Scenario scenario;
	private String nome;

	public MainSimulazione() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	// GETTER ///////////////////////////////

	public CityGraph getGrafo() {
		return grafo;
	}

	public double getNumeroVeicoli() {
		return numeroVeicoli;
	}

	public String getNome() {
		return nome;
	}

	public double getPeriodoGenerazione() {
		return periodoGenerazione;
	}

	// METHODS ///////////////////////////////

	public void start(String directoryXMLGraph) throws InterruptedException {
		this.grafo = CityGraphXMLParser.getGraph("Cosenza", directoryXMLGraph);
		grafo.display().disableAutoLayout();
		// this.numeroVeicoli = numeroVeicoli;
		// this.periodoGenerazione = periodoDiGenerazione;
		// this.scenario = scenario;
		this.nome = "seed[" + Param.seed + "]";
		// Param.resetRandom();
		Param.setSeed(389);
		LinkedList<Vehicle> listaVeicoli = new LinkedList<>();
		Vehicle v = null;
		for (int i = 0; i < numeroVeicoli; i++) {
			v = (Vehicle) grafo.addMobileNode(i, Macchina.class);
			listaVeicoli.add(v);
			v.setAttribute("ui.label", v.getId());

		}
		Regole regole = scenario.getRegole();
		regole.applicaRegole(this, listaVeicoli);
		grafo.startSimulation();
	}

	public File chooseFile() {
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

		fc.setCurrentDirectory(new File(System.getProperty("user.dir") + File.separator + "XMLConfig"));
		int n = fc.showOpenDialog(this);

		if (n == JFileChooser.APPROVE_OPTION) return fc.getSelectedFile();
		else System.exit(-1);
		return null;

	}

	public void inputParam() {
		JPanel p = new JPanel(new GridLayout(4, 2, 5,10));
		JTextField veicoli = new JTextField(10);
		JTextField perGenerazione = new JTextField(10);

		JComboBox scenarioScelto = new JComboBox(Scenario.values());
		String [] semafori = {"SEMAFORO CLASSICO","SEMAFORO A SOGLIA"}; 
		JComboBox semaforoScelto = new JComboBox(semafori);

		p.add(new JLabel("Numero di veicoli: "));
		p.add(veicoli);
		p.add(new JLabel("tempo generazione veicoli (in millisecondi): "));
		p.add(perGenerazione);
		p.add(new JLabel("Scegli scenario: "));
		p.add(scenarioScelto);
		scenarioScelto.setBackground(new Color(255,255,255));
		p.add(new JLabel("Scegli tipo di semaforo: "));
		p.add(semaforoScelto);
		semaforoScelto.setBackground(new Color(255,255,255));

		do {
			int res = JOptionPane.showConfirmDialog(null, p, "PARAMETRI DI INPUT: ", JOptionPane.OK_CANCEL_OPTION);
			if (res == 0) {
				if (veicoli.getText().matches("[0-9]*") && !veicoli.getText().trim().equals(""))
					numeroVeicoli = Integer.parseInt(veicoli.getText());
				else numeroVeicoli = 0;

				if (perGenerazione.getText().matches("[0-9]*") && !perGenerazione.getText().trim().equals(""))
					periodoGenerazione = Double.parseDouble(perGenerazione.getText());
				else periodoGenerazione = 0;
				
				if(semaforoScelto.getSelectedItem().equals("SEMAFORO CLASSICO")) Param.setSemaforo(true);
				else Param.setSemaforo(false);
				
				scenario = (Scenario) scenarioScelto.getSelectedItem();
				
				
			} else System.exit(-1);
			String errore = "";
			if (numeroVeicoli == 0 || periodoGenerazione == 0) {
				errore = "Parametri di input sbagliati!\nNumero di veicoli deve essere un valore numerico compreso tra 1 e 10.000\n"
								+ "Periodo di generazione deve essere un valore numerico compreso tra 1 e 10.000";
				JOptionPane.showMessageDialog(new JPanel(), errore, "ERRORE", JOptionPane.ERROR_MESSAGE);
			}
		} while (numeroVeicoli == 0 || periodoGenerazione == 0);
	}


	public static void main(String[] args) throws IOException {
		MainSimulazione sim = new MainSimulazione();
		String directory = sim.chooseFile().toString();
		sim.inputParam();
		try {
			sim.start(directory);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}
