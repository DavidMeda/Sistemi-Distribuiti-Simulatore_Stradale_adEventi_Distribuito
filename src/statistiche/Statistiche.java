package statistiche;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.util.Set;
import java.util.TreeSet;
import java.util.jar.JarInputStream;
import ITS.RSU.RemoteRSU;
import util.Param;

public class Statistiche extends UnicastRemoteObject implements ServerStatistiche {

	private HashMap<RemoteRSU, VariabileStatRSU> mappaStatistiche = new HashMap<RemoteRSU, VariabileStatRSU>();
	private Set<VariabileStatVeicolo> listaStatVeicoli = new TreeSet<VariabileStatVeicolo>();
	private static final long serialVersionUID = -6219374298605448637L;
	private boolean nuovaSesione = true;

	// variabili RSU
	private String idRSUMessMax, idRSUMessMin, idRSUGradoOttMin, idRSUGradoOttMax;
	private int RSUtot = 0, numMessMassimo = Integer.MIN_VALUE, numMessMinimo = Integer.MAX_VALUE, sommaMessTotali = 0,
					sommaMessInviatiRSU_RSU = 0, sommaMessRicevutiRSU_RSU = 0, sommaMessInviatiRSU_Veicoli = 0,
					sommaMessRicevutiRSU_Veicoli = 0, mediaPersorsiMinimi = 0, mediaPercorsiTot = 0,
					mediaMessRicevuti = 0, mediaMessInviati = 0, mediaMessTot = 0;
	private double sommaGradoOttimalitaPercorsiMinimi = 0.0, sommaGradoCongestione = 0.0, mediaGradoCongestione = 0.0,
					mediaGradoOttimalitaPercorsiMinimi = 0.0, mediaDurataTotale = 0.0, durataTotale = 0.0,
					gradoOttMin = Double.MAX_VALUE, gradoOttMax = Double.MIN_VALUE;

	// variabili veicoli
	private String idVeicoloAttesaMassima = "", idVeicoloAttesaMinima = "";
	private double tempoDiAttesaMinimo = Double.MAX_VALUE, tempoDiAttesaMassimo = Double.MIN_VALUE;
	private double distanzaPercorsaMedia = 0.0, tempoTotalePercorrenzaMedio = 0.0, tempoTotaleAttesaMedio = 0.0;
	private int numTotaleVeicoli = 0, numAttraversamentiMedio = 0, numSemaforiRossiMedio = 0, numSemaforiVerdiMedio = 0;

	public Statistiche() throws RemoteException {
		super();
	}

	@Override
	public synchronized void updateStatisticheRSU(RemoteRSU RSU, VariabileStatRSU var) throws RemoteException {
		// System.out.println("Ricevo aggiornamento statistiche da "+RSU.getNameRSU());
		mappaStatistiche.put(RSU, var);
	}

	public synchronized void updateStatisticheVeicolo(VariabileStatVeicolo varVeicol) throws RemoteException {
		listaStatVeicoli.add(varVeicol);
	}

	public synchronized void richiestaStatisticheGenerali() throws RemoteException {
		RSUtot++;
		if (RSUtot >= (mappaStatistiche.size())) {
			calcolaStatisticheGenerali();
			nuovaSesione = true;
		}
	}

	public synchronized void registraRSU(RemoteRSU rsu) throws RemoteException {
		if (nuovaSesione) {
			reset();
			nuovaSesione = false;
		}
		mappaStatistiche.put(rsu, null);
		// System.out.println("si è aggiunto " + rsu.getNameRSU() + " size= " +
		// mappaStatistiche.size());
	}

	private void calcolaStatisticheGenerali() throws RemoteException {
		updateStatRSU();
		updateStatVeicoli();
		StringBuilder sb = new StringBuilder();
		sb.append("\nSTATISTICHE SU VEICOLI TOTALE = " + numTotaleVeicoli);
		sb.append("\n\t- Distanza media percorsa [metri]: " + String.format("%4.3f", distanzaPercorsaMedia));
		sb.append("\n\t- Tempo di percorrenza medio [secondi]: " + String.format("%4.3f", tempoTotalePercorrenzaMedio));
		sb.append("\n\t- Tempo di attesa medio [secondi]: " + String.format("%4.3f", tempoTotaleAttesaMedio));
		sb.append("\n\t- Numero medio di strade attraversate: " + numAttraversamentiMedio);
		sb.append("\n\t- Numero medio di semafori rossi incotrati: " + numSemaforiRossiMedio);
		sb.append("\n\t- Numero medio di semafori verdi incotrati: " + numSemaforiVerdiMedio);
		sb.append("\n\t- Veicolo con il minor tempo di attesa: " + idVeicoloAttesaMinima + " [" + tempoDiAttesaMinimo
						+ " sec]");
		sb.append("\n\t- Veicolo con il maggior tempo di attesa: " + idVeicoloAttesaMassima + " ["
						+ tempoDiAttesaMassimo + " sec]");
		sb.append("\nSTATISTICHE SU RSU TOTALI = " + RSUtot);
		sb.append("\n\t- Media messaggi ricevuti per RSU = " + mediaMessRicevuti);
		sb.append("\n\t- Media messaggi inviati per RSU = " + mediaMessInviati);
		sb.append("\n\t- RSU col maggior numero di messaggi = " + idRSUMessMax + " (num: " + numMessMassimo + ")");
		sb.append("\n\t- RSU col minor numero di messaggi = " + idRSUMessMin + " (num: " + numMessMinimo + ")");
		sb.append("\n\t- Sommatoria messaggi totali = " + sommaMessTotali);
		sb.append("\n\t- Sommatoria messaggi ricevuti da RSU = " + sommaMessRicevutiRSU_RSU);
		sb.append("\n\t- Sommatoria messaggi ricevuti da Veicoli = " + sommaMessRicevutiRSU_Veicoli);
		sb.append("\n\t- Sommatoria messaggi inviati a RSU = " + sommaMessInviatiRSU_RSU);
		sb.append("\n\t- Sommatoria messaggi Inviati a Veicoli = " + sommaMessInviatiRSU_Veicoli);
		// sb.append("\n\t- Media grado congestione della rete = " + mediaGradoCongestione);
		sb.append("\n\t- Percentuale ottimalità percorsi minimi = " + mediaGradoOttimalitaPercorsiMinimi);
		sb.append("\n\t- RSU col minor grado di ottimalità sui percorsi minimi = " + idRSUGradoOttMin + " (num: "
						+ gradoOttMin + ")");
		sb.append("\n\t- RSU col maggior grado di ottimalità sui percorsi minimi = " + idRSUGradoOttMax + " (num: "
						+ gradoOttMax + ")\n");
		sb.append("\nSTATISTICHE PER VEICOLO");
		for (VariabileStatVeicolo var : listaStatVeicoli) {
			sb.append("\n" + var);
			// System.out.println(var);
		}
		String statistiche = sb.toString();
		// System.out.println("\n\n\n");
		// System.out.println(sb.toString());
		for (Entry<RemoteRSU, VariabileStatRSU> e : mappaStatistiche.entrySet()) {
			e.getKey().stampaStatistiche(statistiche);
		}
		displayText(statistiche);

	}

	private void displayText(String statistiche) {
		JFrame frame = new JFrame("SERVER STATISTICHE");
		// Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		// frame.setLocation(dim.width / 2 - frame.getSize().width / 2, dim.height / 2 -
		// frame.getSize().height / 2);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JTextArea textArea = new JTextArea("STATISTICHE GENERALI");
		JScrollPane scrollPanel = new JScrollPane(textArea);
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		Font font = new Font(textArea.getFont().getName(), Font.BOLD, 17);
		textArea.setFont(font);
		frame.add(scrollPanel, BorderLayout.CENTER);
		JButton b = new JButton("SALVA ED ESCI");
		b.setPreferredSize(new Dimension(40, 40));
		b.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String nomeFile = JOptionPane.showInputDialog(new JFrame("SALVATAGGIO"), "Scegli nome file da memorizzare");
				try {
					salvaStatistiche(statistiche, nomeFile);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				frame.dispose();

			}
		});

		frame.add(b, BorderLayout.SOUTH);
		int x = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		int y = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() - 50;
		frame.setSize(x, y);
		frame.setVisible(true);
		textArea.append(statistiche);
	}

	private void updateStatVeicoli() {
		numTotaleVeicoli = listaStatVeicoli.size();
		double distanzaPercorsa = 0.0, tempoTotalePercorrenza = 0.0, tempoTotaleAttesa = 0.0;
		int numAttraversamenti = 0, numSemaforiRossi = 0, numSemaforiVerdi = 0;

		for (VariabileStatVeicolo var : listaStatVeicoli) {
			distanzaPercorsa += var.getDistanzaPercorsa();
			tempoTotalePercorrenza += var.getTempoTotalePercorrenza();

			if (var.getTempoTotaleAttesa() < tempoDiAttesaMinimo) {
				tempoDiAttesaMinimo = var.getTempoTotaleAttesa();
				idVeicoloAttesaMinima = var.getIDveicolo();
			}

			if (var.getTempoTotaleAttesa() > tempoDiAttesaMassimo) {
				tempoDiAttesaMassimo = var.getTempoTotaleAttesa();
				idVeicoloAttesaMassima = var.getIDveicolo();
			}
			tempoTotaleAttesa += var.getTempoTotaleAttesa();
			numAttraversamenti += var.getNumAttraversamenti();
			numSemaforiRossi += var.getNumSemaforiRossi();
			numSemaforiVerdi += var.getNumSemaforiVerdi();
		}
		distanzaPercorsaMedia = distanzaPercorsa / numTotaleVeicoli;
		tempoTotalePercorrenzaMedio = tempoTotalePercorrenza / numTotaleVeicoli;
		tempoTotaleAttesaMedio = tempoTotaleAttesa / numTotaleVeicoli;
		numAttraversamentiMedio = numAttraversamenti / numTotaleVeicoli;
		numSemaforiRossiMedio = numSemaforiRossi / numTotaleVeicoli;
		numSemaforiVerdiMedio = numSemaforiVerdi / numTotaleVeicoli;

	}

	private void updateStatRSU() throws RemoteException {
		VariabileStatRSU var;
		int countOtti = 0;
		for (Entry<RemoteRSU, VariabileStatRSU> e : mappaStatistiche.entrySet()) {
			var = e.getValue();
			if (var.getNumeroMessTotali() > 0 && var.getNumeroMessTotali() < numMessMinimo) {
				numMessMinimo = var.getNumeroMessTotali();
				idRSUMessMax = "" + e.getKey().getNameRSU();
			}
			if (var.getNumeroMessTotali() > numMessMassimo) {
				numMessMassimo = var.getNumeroMessTotali();
				idRSUMessMin = "" + e.getKey().getNameRSU();
			}
			sommaMessTotali += var.getNumeroMessTotali();
			sommaMessRicevutiRSU_RSU += var.getNumeroMessRicevutiRSU_RSU();
			sommaMessInviatiRSU_RSU += var.getNumeroMessRicevutiRSU_RSU();
			sommaMessInviatiRSU_Veicoli += var.getNumeroMessInviatiRSU_Veicolo();
			sommaMessRicevutiRSU_Veicoli += var.getNumeroMessRicevutiRSU_Veicolo();
			durataTotale = var.getDurataTotale();

			if (var.getGradoOttimalitaPercorsiMin() > 0) {
				sommaGradoOttimalitaPercorsiMinimi += var.getGradoOttimalitaPercorsiMin();
				countOtti++;

				if (var.getGradoOttimalitaPercorsiMin() > gradoOttMax) {
					gradoOttMax = var.getGradoOttimalitaPercorsiMin();
					idRSUGradoOttMax = "" + e.getKey().getNameRSU();
				}

				if (var.getGradoOttimalitaPercorsiMin() < gradoOttMin) {
					gradoOttMin = var.getGradoOttimalitaPercorsiMin();
					idRSUGradoOttMin = "" + e.getKey().getNameRSU();
				}
			}
			mediaMessRicevuti = (sommaMessRicevutiRSU_RSU + sommaMessRicevutiRSU_Veicoli) / RSUtot;
			mediaMessInviati = (sommaMessInviatiRSU_RSU + sommaMessInviatiRSU_Veicoli) / RSUtot;
			mediaMessTot = sommaMessTotali / RSUtot;
			mediaDurataTotale = durataTotale / RSUtot;
			mediaGradoOttimalitaPercorsiMinimi = (sommaGradoOttimalitaPercorsiMinimi / countOtti) * 100;
		}
	}

	private void reset() {

		mappaStatistiche.clear();
		listaStatVeicoli.clear();
		mappaStatistiche = new HashMap<RemoteRSU, VariabileStatRSU>();
		listaStatVeicoli = new TreeSet<VariabileStatVeicolo>();

		RSUtot = 0;
		numMessMassimo = Integer.MIN_VALUE;
		numMessMinimo = Integer.MAX_VALUE;
		sommaMessTotali = 0;
		sommaMessInviatiRSU_RSU = 0;
		sommaMessRicevutiRSU_RSU = 0;
		sommaMessInviatiRSU_Veicoli = 0;
		sommaMessRicevutiRSU_Veicoli = 0;
		sommaGradoOttimalitaPercorsiMinimi = 0;
		sommaGradoCongestione = 0;
		mediaGradoCongestione = 0;
		mediaGradoOttimalitaPercorsiMinimi = 0;
		mediaPersorsiMinimi = 0;
		mediaPercorsiTot = 0;
		mediaMessRicevuti = 0;
		mediaMessInviati = 0;
		mediaMessTot = 0;

		idVeicoloAttesaMassima = "";
		idVeicoloAttesaMinima = "";
		tempoDiAttesaMinimo = Double.MAX_VALUE;
		tempoDiAttesaMassimo = Double.MIN_VALUE;
		distanzaPercorsaMedia = 0.0;
		tempoTotalePercorrenzaMedio = 0.0;
		tempoTotaleAttesaMedio = 0.0;
		numTotaleVeicoli = 0;
		numAttraversamentiMedio = 0;
		numSemaforiRossiMedio = 0;
		numSemaforiVerdiMedio = 0;
	}
	
	private static void salvaStatistiche(String statistiche, String nomeFile) throws IOException {
		String path = new File(System.getProperty("user.dir")+File.separator+"dati statistiche").toString();
		
		if (!(new File(path)).isDirectory() ){
			boolean f = new File(path).mkdirs();
			if(!f)throw new IOException("non si può creare la cartella");
		}
			try {
				
				ObjectOutputStream o = null;
				o = new ObjectOutputStream(new FileOutputStream(path+File.separator+nomeFile+".txt"));
				o.writeObject(statistiche);
				o.close();
				
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null,"impossibile creare file "+path);
			}
	}

}
