package ITS.regolatoriSemafori;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import ITS.RSU.RSU;
import ITS.semafori.Semaforo;
import network.NetEdge;
import network.NetNode;

public class RegolatoreClassico extends ABS_Regolatore {

	public RegolatoreClassico(RSU rsu, List<NetEdge> archiEntranti) {
		super(rsu, archiEntranti, 0);
	}

	@Override
	// from abs_regol
	public void init() {
		// assegna un semaforo per ogni fase
		Semaforo s = null;
		LinkedList<Semaforo> ls = null;
		ArrayList<Fase> fasi = new ArrayList<>(semafori.size());
		for (Entry<NetNode, ArrayList<Semaforo>> e : semafori.entrySet()) {
			s = e.getValue().get(0);
			ls = new LinkedList<>();
			ls.add(s);
			fasi.add(new Fase(s));
		}

		/*
		 * print* System.out.println(this+": init."); System.out.println("-- fasi= "+fasi); /
		 **/
		setFasi(fasi);

		// inizia il ciclo delle fasi
		super.init();
		nextPhase();

	}

}
