package simulazione.Launcher;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import javax.imageio.ImageIO;
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

public class MainSimulazione {

	private static final long serialVersionUID = 1L;
	private CityGraph grafo;
	private double numeroVeicoli = 0.0;
	private double periodoGenerazione = 0.0;
	private Scenario scenario;
	private String nome;
	private JFrame frame = new JFrame("Simulatore");
	private boolean temp = true;

	public MainSimulazione() {
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private class MainPanel extends JPanel {

		private BufferedImage sfondo = null;

		public MainPanel() {
			try {
				sfondo = ImageIO.read(getClass().getResource("/sfondoMain.png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			setSize(1388, 786);
			setLayout(null);
			MouseListener listener = new MouseAdapter() {

				public void mouseClicked(MouseEvent e) {
					temp = false;
					frame.dispose();
				}
			};
			addMouseListener(listener);
		}

		public void paintComponent(Graphics g) {
			g.drawImage(sfondo, 0, 0, null);
		}
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
		this.grafo = (CityGraph) CityGraphXMLParser.getGraph("Cosenza", directoryXMLGraph);
		grafo.display().disableAutoLayout();
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

	public void sfondo() {
		JPanel p = new MainPanel();

		frame.setSize(1220, 740);
		int x = Toolkit.getDefaultToolkit().getScreenSize().width / 2;
		int y = Toolkit.getDefaultToolkit().getScreenSize().height / 2;
		frame.setBounds(x - (1220 / 2), y - (740 / 2), 1220, 740);
		frame.add(p);
		frame.setVisible(true);
		while (temp) {
			frame.revalidate();
			frame.repaint();
		}

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
		int n = fc.showOpenDialog(frame);

		if (n == JFileChooser.APPROVE_OPTION) return fc.getSelectedFile();
		else System.exit(-1);
		return null;

	}

	public void inputParam() {
		JPanel p = new JPanel(new GridLayout(4, 2, 5, 10));

		JTextField veicoli = new JTextField(10);
		JTextField perGenerazione = new JTextField(10);
		JComboBox scenarioScelto = new JComboBox(Scenario.values());
		String[] semafori = { "SEMAFORO CLASSICO", "SEMAFORO A SOGLIA" };
		JComboBox semaforoScelto = new JComboBox(semafori);

		JLabel veic = new JLabel("Inserire numero di veicoli: ");
		veic.setFont(new Font(Font.SANS_SERIF, Font.CENTER_BASELINE, 15));
		p.add(veic);
		p.add(veicoli);

		JLabel tempo = new JLabel("Inserire tempo generazione veicoli (in millis): ");
		tempo.setFont(new Font(Font.SANS_SERIF, Font.CENTER_BASELINE, 15));
		p.add(tempo);
		p.add(perGenerazione);

		JLabel scen = new JLabel("Scegliere scenario: ");
		scen.setFont(new Font(Font.SANS_SERIF, Font.CENTER_BASELINE, 15));
		p.add(scen);
		p.add(scenarioScelto);

		JLabel sem = new JLabel("Scegliere il tipo di semaforo: ");
		sem.setFont(new Font(Font.SANS_SERIF, Font.CENTER_BASELINE, 15));
		p.add(sem);
		p.add(semaforoScelto);

		do {
			int res = JOptionPane.showConfirmDialog(null, p, "PARAMETRI DI INPUT: ", JOptionPane.OK_CANCEL_OPTION);
			if (res == 0) {
				if (veicoli.getText().matches("[0-9]*") && !veicoli.getText().trim().equals(""))
					numeroVeicoli = Integer.parseInt(veicoli.getText());
				else numeroVeicoli = 0;

				if (perGenerazione.getText().matches("[0-9]*") && !perGenerazione.getText().trim().equals(""))
					periodoGenerazione = Double.parseDouble(perGenerazione.getText());
				else periodoGenerazione = 1;

				if (semaforoScelto.getSelectedItem().equals("SEMAFORO CLASSICO")) Param.setSemaforo(true);
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
		sim.sfondo();
		String directory = sim.chooseFile().toString();
		sim.inputParam();
		try {
			sim.start(directory);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}
