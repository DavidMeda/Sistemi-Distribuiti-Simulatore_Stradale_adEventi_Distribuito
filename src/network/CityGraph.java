package network;

import java.util.HashMap;
import java.util.LinkedList;
import org.graphstream.graph.Edge;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;
import events.UpdatePosition;
import util.Param;
import veicoli.Vehicle;

public class CityGraph extends NetGraph {

	private HashMap<NetEdge, LinkedList<Vehicle>> vehiclesAttached = new HashMap<>();
	private LinkedList<MobileNode> mobileNodes = new LinkedList<>(), nodiInMovimento = new LinkedList<>();
	private SpriteManager vehicleManager;
	private LinkedList<Vehicle> veicoliDaRimuovere = new LinkedList<>();
	public static int cont = 0;

	// COSTR ////////////////////////////////

	public CityGraph(String name) {
		super(name);
		vehicleManager = new SpriteManager(this);

	}

	// GETTER ///////////////////////////////////
	public LinkedList<MobileNode> getNodiInMovimento() {
		return nodiInMovimento;
	}

	public HashMap<NetEdge, LinkedList<Vehicle>> getVehicles() {
		return vehiclesAttached;
	}

	public int getNumVeicoliTot() {
		return mobileNodes.size();
	}

	public LinkedList<MobileNode> getMobileNodes() {
		return mobileNodes;
	}

	public LinkedList<Vehicle> getVehiclesOnTheEdge(String idEdge) {
		NetEdge e = getEdge(idEdge);
		return vehiclesAttached.get(e);
	}

	// METHODS //////////////////////////////////

	public void setNodoInMovimento(MobileNode nodoMobile) {
		mobileNodes.remove(nodoMobile);
		nodiInMovimento.add(nodoMobile);

	}

	public void rimuoviVeicolo(Vehicle veichle) {
		veicoliDaRimuovere.add(veichle);
	}

	public void updateMobileNode() {
		for (MobileNode n : nodiInMovimento) {
			n.move();
		}

		getScheduler().addEvent(new UpdatePosition(this, Param.updatePositionTime));

	}

	@Override
	public void init() {
		for (Edge e : getEachEdge()) {
			vehiclesAttached.put((NetEdge) e, new LinkedList<>());
		}
		getScheduler().addEvent(new UpdatePosition(this, Param.updatePositionTime));

		super.init();

	}

	public void attachVehicleToEdge(String idEdge, Vehicle vehicle) {
		NetEdge e = getEdge(idEdge);
		vehiclesAttached.get(e).add(vehicle);

		/*
		 * print* System.out.println("citygraph.attachtoedge. aggiunto "+vehicle+" all'arco "
		 * +idEdge+" --> "+vehiclesAttached.get(e)); /
		 **/
	}

	public boolean removeVehicleFromEdge(String idEdge, MobileNode mobileNode) {
		NetEdge e = getEdge(idEdge);
		return vehiclesAttached.get(e).remove(mobileNode);

	}

	public Sprite addMobileNode(Object id, Class<? extends MobileNode> mobileNodeClass) {
		/*
		 * print* System.out.println(this+": addMobileNode");
		 * System.out.println(this+": id nodo mobile = "+id.toString());
		 * System.out.println(this+": scheduler = "+getScheduler()); /
		 **/

		MobileNode s = vehicleManager.addSprite(id.toString(), mobileNodeClass);
		// s.setScheduler(getScheduler());
		s.setGraph(this).setID(id);
		mobileNodes.add(s);
		return s;

	}

	// arrivati a destinazione rimuovo i veicoli e li metto a null
	public void removeMobileNode(MobileNode mobileNode) {
		NetEdge e = (NetEdge) mobileNode.getAttachment();

		removeVehicleFromEdge(e.getId(), mobileNode);
		mobileNodes.remove(mobileNode);
		nodiInMovimento.remove(mobileNode);
		vehicleManager.removeSprite(mobileNode.getId());
		mobileNode = null;
		if (mobileNodes.size() == 0 && nodiInMovimento.size() == 0) getScheduler().stopSimulation();
	}

	// return true se l'arco è pieno di veicoli
	public boolean edgeIsFull(NetEdge edge) {
		int numMaxVehicle = (int) ((double) edge.getAttribute("length") / (Param.distanzaInterveicolo));

		int numVehicleEdge = vehiclesAttached.get(edge).size();
		return !(numVehicleEdge < numMaxVehicle);

	}

}