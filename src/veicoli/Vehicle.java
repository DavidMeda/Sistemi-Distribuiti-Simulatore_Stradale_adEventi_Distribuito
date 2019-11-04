package veicoli;

import java.util.LinkedList;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.Units;
import network.NetEdge;
import network.NetNode;
import network.message.Message;
import statistiche.VariabileStatVeicolo;
import util.Param;
import network.*;

// Vehicles are object that move on the edge
public abstract class Vehicle extends MobileNode implements Comparable<Vehicle> {

	// indica l'ultimo rsu a cui mi sono registrato
	protected NetNode registeredNode;
	// indica il path da seguire
	protected LinkedList<NetEdge> path = new LinkedList<>();
	// indica l'arco in cui mi trovo
	protected NetEdge currentEdge;

	protected boolean
	// indica se l'auto è in movimento
	moving = false,
	// indica se è arrivato il verde dal semaforo alla fine di current edge
	verdeAlSemaforo = false, fermoAlPrimoNodo = true;

	protected double
	// indica la percentuale di percorrenza dell'arco
	// (se la macchina è a metà  arco: position = 0.5, se è a fine arco: position = 1.0 )
	position,
	// tempo di ingresso nell'arco
	ingressTimeOnCurrentEdge;

	protected NetNode
	// indica il nodo di partenza del veicolo
	startingNode,
	// indica l'ultimo nodo attraversato
	currentNode,
	// indica il prossimo nodo da attraversare
	nextNode,
	// indica il nodo di destinazione del veicolo
	destinationNode;

	// parametri statistici
	protected double
	// tempo di attesa ai semafori
	tempoDiAttesaTotale = 0,
	// tempo totale in cui il veicolo è stato fermo su un arco
	tempoDiAttesaSuArco = 0,
	// ora di ingresso del veicolo nel grafo
	startTime,
	// indica la distanza percorsa
	distanceTraveled = 0; // metri
	
	protected VariabileStatVeicolo variabileStat;

	//////////////////////////

	public Vehicle() {}

	public Vehicle(CityGraph graph, String name) {
		super(graph, name);
	}

	/// GETTER //////////////
	public NetNode getStartingNode() {
		return startingNode;
	}

	public NetNode getCurrentNode() {
		return currentNode;
	}

	public NetNode getNextNode() {
		return nextNode;
	}

	public NetNode getTargetNode() {
		return destinationNode;
	}

	public NetEdge getCurrentEdge() {
		return currentEdge;
	}
	
	

	// SETTER ///////////////
	public Vehicle setStartingNode(int index) {
		/*
		 * print* System.out.println(this+": set nodo iniziale = "+getGraph().getNode(index)); /
		 **/
		startingNode = getGraph().getNode(index);
		setCurrentNode(index);
		variabileStat = new VariabileStatVeicolo(id);
		return this;

	}

	public Vehicle setCurrentNode(int index) {
		currentNode = getGraph().getNode(index);
		return this;

	}

	public void setCurrentNode(NetNode node) {
		registeredNode = node;
	}

	public Vehicle setTargetNode(int index) {
		destinationNode = getGraph().getNode(index);
		return this;

	}

	// METHODS ////////////////////////////
	

	public double getPositionOnTheEdge() {
		return position * ((Double) currentEdge.getAttribute("length"));
	}

	private boolean ciSonoVeicoliDavanti() {
		double onTheEdge = currentEdge.getAttribute("length");
		double distanceFromVehicle;

		// non posso muovermi se sono troppo vicino al prossimo veicolo
		for (Vehicle v : getGraph().getVehiclesOnTheEdge(currentEdge.getId())) {
			// distanza tra i veicoli
			distanceFromVehicle = (v.position * onTheEdge) - (this.position * onTheEdge);

			// se il veicolo analizzato è dietro di me, analizza il prossimo veicolo
			if (distanceFromVehicle <= 0 || v == this) {
				continue;
			}

			// altrimenti
			else if (distanceFromVehicle <= Param.distanzaInterveicolo) {
				/*
				 * print* if(getId().equals("1")) { System.out.println();
				 * System.out.println(this+": non posso muovermi perchè sono troppo vicino a "+v); } /
				 **/

				return true;
			}
		}
		return false;

	}

	@Override
	public void move() {
		if (fermoAlPrimoNodo) {

			/*
			 * print* System.out.println(); System.out.println(this+": sono fermo al primo nodo"); /
			 **/

			// se è stato assegnato un percorso al veicolo
			if (path.size() > 0) {
				// controllo che l'arco non sia pieno
				fermoAlPrimoNodo = ((path.getFirst() != null) && getGraph().edgeIsFull(path.getFirst()));

				/*
				 * print* System.out.println(this+": sono ancora fermo al primo nodo? "+fermoAlPrimoNodo);
				 * /
				 **/

				// se l'arco è pieno non mi muovo
				if (fermoAlPrimoNodo) return;

				// altrimenti inizio a muovermi

				// tempo in cui il veicolo inizia a muoversi per la prima volta
				startTime = getScheduler().getCurrentTime();

				// tempo di ingresso sull'arco
				ingressTimeOnCurrentEdge = startTime;
				tempoDiAttesaSuArco = 0;

				// assegno il primo arco
				currentEdge = path.removeFirst();

				currentNode = currentEdge.getSourceNode();
				nextNode = currentEdge.getTargetNode();

				distanceTraveled += (double) currentEdge.getAttribute("length");
				variabileStat.updateDistanzaPercorsa(distanceTraveled);
				attachToEdge(currentEdge.getId());

				// comunico al primo RSU che inizio a muovermi
				Message cambioArco = new Message("CAMBIO ARCO", this, registeredNode, Param.elaborationTime);
				cambioArco.setData(currentNode, nextNode);
				sendEvent(cambioArco);
				variabileStat.updateNumAttraversamenti();

				/*
				 * print* System.out.println(this+": inizio a muovermi per la prima volta sull'arco "
				 * +currentEdge+" a "+startTime); /
				 **/

			}

			// se non è stato assegnato un percorso al veicolo sono ancora fermo al primo nodo
			else return;
		}

		// se non posso muovermi
		if (ciSonoVeicoliDavanti()) {
			// conto il tempo di attesa sull'arco
			tempoDiAttesaSuArco += Param.updatePositionTime;

			/*
			 * print* if(getId().equals("4") || getId().equals("2"))
			 * System.out.println(this+": sono fermo dietro ad un veicolo"+" tempo attesa "
			 * +tempoDiAttesaSuArco); /
			 **/

			return;
		}
		// se posso muovermi
		else {
			// se sono a fine arco
			if (position >= 1) {
				// se sono arrivato a destinazione
				if (path.getFirst() == null) {

					// aggiorno i parametri statistici
					tempoDiAttesaTotale += tempoDiAttesaSuArco;
					double tempoTotale = getScheduler().getCurrentTime() - startTime;
					variabileStat.updateTempoTotAttesa(tempoDiAttesaTotale);
					variabileStat.updateTempoTot(tempoTotale);
					// comunico che il veicolo ha abbandonato l'arco
					Message destinazione = new Message("DESTINAZIONE", this, registeredNode, Param.elaborationTime);
					destinazione.setData(currentEdge.getSourceNode(), variabileStat);
					sendEvent(destinazione);
					/*
					 * print* System.out.println("\n"+
					 * this+": sono arrivato a destinazione, tempo di attesa totale = "+tempoDiAttesaTotale);
					 * /
					 **/
					return;
				}

				// se il semaforo è rosso resto fermo
				if (!verdeAlSemaforo) {
					tempoDiAttesaSuArco += Param.updatePositionTime;
					/*
					 * print* if(getId().equals("4") || getId().equals("2"))
					 * System.out.println(this+": il semaforo è ROSSO su "+currentEdge+" tempo attesa "
					 * +tempoDiAttesaSuArco); /
					 **/

					return;
				}
				
				// se il prossimo arco è pieno resto fermo
				if (getGraph().edgeIsFull(path.getFirst())) {
					tempoDiAttesaSuArco += Param.updatePositionTime;

					/*
					 * print* if(getId().equals("4") || getId().equals("2")) {
					 * System.out.println(this+": sono a FINE ARCO ma l'arco "+path.getFirst()+" è PIENO"
					 * +" tempo attesa "+tempoDiAttesaSuArco); }/
					 **/
					return;
				}
				// cambio arco
				NetNode nodoProvenienza = currentEdge.getSourceNode();

				currentEdge = path.removeFirst();
				currentNode = currentEdge.getSourceNode();

				nextNode = currentEdge.getTargetNode();

				NetNode nodoSuccessuvo = nextNode;

				// aggiorno i parametri
				ingressTimeOnCurrentEdge = getScheduler().getCurrentTime();
				tempoDiAttesaTotale += tempoDiAttesaSuArco;
				tempoDiAttesaSuArco = 0;
				distanceTraveled += (double) currentEdge.getAttribute("length");
				variabileStat.updateDistanzaPercorsa(distanceTraveled);

				// numNodiAttraversati++;
				attachToEdge(currentEdge.getId());

				// comunico il cambio arco all'RSU
				Message cambioArco = new Message("CAMBIO ARCO", this, registeredNode, Param.elaborationTime);
				cambioArco.setData(nodoProvenienza, nodoSuccessuvo);
				sendEvent(cambioArco);
				variabileStat.updateNumAttraversamenti();
				/*
				 * print* System.out.println(this+": invio cambio arco sono su "+currentEdge+"  "); /
				 **/

				/*
				 * print* if(getId().equals("5")){
				 * System.out.println(this+": sono in mezzo l'ARCO "+currentEdge);
				 * System.out.println(this+": tempo di attesa totale = "+tempoDiAttesaTotale); } /
				 **/
			}

			// aggiorno posizione sull'arco
			position = relativePosition();
			setPosition(position);

		}

	}

	public double getY() {
		if (getNextNode() == null) { return Double.MAX_VALUE; }

		// x,y position next node
		double x1 = plsGiveMeThe(nextNode).getAttribute("x");
		double y1 = plsGiveMeThe(nextNode).getAttribute("y");
		// x,y position prev node
		double x0 = plsGiveMeThe(currentNode).getAttribute("x");
		double y0 = plsGiveMeThe(currentNode).getAttribute("y");
		// x,y
		double x = x1 - x0;
		double y = y1 - y0;
		// alfa <
		double alfa = Math.atan2(y, x);

		double onTheEdge = currentEdge.getAttribute("length");

		double ipotenusa = position * onTheEdge; // Ù©(â—�Ì®Ì®Ìƒâ€¢)Û¶

		return y0 + (ipotenusa * Math.sin(alfa));

	}
	@Deprecated
	private NetNode plsGiveMeThe(NetNode nodeInTheEdge) {
		// i don't know why but the node in the edges haven't attributes
		String id = nodeInTheEdge.getId();
		return getGraph().getNode(id);

	}

	public double getX() {
		if (getNextNode() == null) { return Double.POSITIVE_INFINITY; }

		// x,y position next node
		double x1 = plsGiveMeThe(nextNode).getAttribute("x");
		double y1 = plsGiveMeThe(nextNode).getAttribute("y");
		// x,y position prev node
		double x0 = plsGiveMeThe(currentNode).getAttribute("x");
		double y0 = plsGiveMeThe(currentNode).getAttribute("y");
		// x,y
		double x = x1 - x0;
		double y = y1 - y0;
		// alfa <
		double alfa = Math.atan2(y, x);

		double onTheEdge = currentEdge.getAttribute("length");

		double ipotenusa = position * onTheEdge;

		return x0 + (ipotenusa * Math.cos(alfa));

	}

	public double relativePosition() {
		double timeOnTheEdge = getScheduler().getCurrentTime() - ingressTimeOnCurrentEdge - tempoDiAttesaSuArco;

		// change velocity to km/h in m/ms
		// K km/h = [K * (10^3 /3.600.000)] m/ms
		double newVelocity = Param.velocitaVeicolo;

		// relative position on the Edge
		double distanceFromSourceNode = newVelocity * timeOnTheEdge;
		double edgeLength = currentEdge.getAttribute("length");

		return distanceFromSourceNode / edgeLength;

	}

	// from sprite
	@Override
	public void setPosition(double percent) {
		setPosition(Units.PX, percent, Param.distanceFromTheEdge, 0);

	}

	@Override
	public void attachToEdge(String id) {
		detach();
		getGraph().attachVehicleToEdge(id, this);
		setPosition(Units.PX, 0, Param.distanceFromTheEdge, 0);

		super.attachToEdge(id);

	}

	@Override
	public void detach() {
		NetEdge attachment = (NetEdge) getAttachment();
		if (attachment != null) {
			String idEdge = attachment.getId();
			getGraph().removeVehicleFromEdge(idEdge, this);
		}
		super.detach();

	}
	public int compareTo(Vehicle v){
		double pippopolino = getPositionOnTheEdge() - v.getPositionOnTheEdge();
		if(pippopolino < 0){
			return 1;
		}else if(pippopolino > 0){
			return -1;
		}
		return 0;
	}

}