package simulazioni;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import network.CityGraph;
import network.CityGraphXMLParser;
import simulazioni.regole.Regole;
import util.Param;
import vanet.Macchina;
import vanet.Vehicle;

public class Simulazione extends JPanel {
	private static final long serialVersionUID = 1L;
	private CityGraph grafo;
	private double numeroVeicoli = 0.0;
	private double periodoGenerazione = 0.0;
	private Scenario scenario;
	private String nome;

	public Simulazione() {}

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
		JPanel p = new JPanel();
		JTextField veicoli = new JTextField(10);
		JTextField perGenerazione = new JTextField(10);

		JComboBox scenarioScelto = new JComboBox(Scenario.values());

		p.add(new JLabel("Numero di veicoli: "));
		p.add(veicoli);
		p.add(new JLabel("tempo generazione veicoli: "));
		p.add(perGenerazione);
		p.add(new JLabel("Scegli scenario: "));
		p.add(scenarioScelto);

		do {
			int res = JOptionPane.showConfirmDialog(null, p, "PARAMETRI DI INPUT: ", JOptionPane.OK_CANCEL_OPTION);
			if (res == 0) {
				if (veicoli.getText().matches("[0-9]*") && !veicoli.getText().trim().equals(""))
					numeroVeicoli = Integer.parseInt(veicoli.getText());
				else numeroVeicoli = 0;

				if (perGenerazione.getText().matches("[0-9]*") && !perGenerazione.getText().trim().equals(""))
					periodoGenerazione = Double.parseDouble(perGenerazione.getText());
				else periodoGenerazione = 0;
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
		// String dir = new
		// File(System.getProperty("user.dir")+File.separator+"XMLConfig\\CityGraph4.xml").toString();
		Simulazione sim = new Simulazione();
		String directory = sim.chooseFile().toString();
		// int numeroVeicoli = 10;
		sim.inputParam();
		try {
			sim.start(directory);

			// sim.start(directory, numeroVeicoli, Param.periodoGenerazione, Scenario.RANDOM);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}
