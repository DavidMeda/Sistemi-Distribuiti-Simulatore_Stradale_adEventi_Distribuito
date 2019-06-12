package vanet;

import java.util.LinkedList;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.Units;
import network.NetEdge;
import network.NetNode;
import network.message.Message;
import util.Param;

//Vehicles are object that move on the edge
public abstract class Vehicle extends MobileNode implements Comparable<Vehicle> {
	
//	protected StatVeicolo stat;
	
	//indica l'ultimo rsu a cui mi sono registrato
	protected NetNode registeredRSU;
	//indica il path da seguire
	protected LinkedList<NetEdge> path = new LinkedList<>();
	//indica l'arco in cui mi trovo
	protected NetEdge currentEdge;
	
	protected boolean 
		//indica se l'auto è in movimento
		moving = false,
		//indica se Ã¨ arrivato il verde dal semaforo in fondo al current edge
		verdeAlSemaforo = false,
		fermoAlPrimoNodo = true;
	
	protected double 
		//indica la percentuale di percorrenza dell'arco
		//(se la macchina è a metà  arco: position = 0.5, se Ã¨ a fine arco: position = 1.0 )
		position,
		//tempo di ingresso nell'arco
		ingressTimeOnCurrentEdge;	

	protected NetNode 
		//indica il nodo di partenza del veicolo
		startingNode,
		//indica l'ultimo nodo attraversato
		currentNode,
		//indica il prossimo nodo da attraversare
		nextNode,
		//indica il nodo di destinazione del veicolo
		destinationNode;
	
	//variabili di ausilio per il calcolo della posizione sull'arco
	protected boolean 
		//indica l'inizio del calcolo del tempo di stop ad un semaforo
		flagStop = false,
		//indica se la macchina Ã¨ arrivata alla fine dell'arco
		flagFineArco = false;

	//variabili di ausilio per il calcolo della posizione sull'arco
	protected double 
		//tempo totale in cui il veicolo Ã¨ stato fermo su un arco
		tempoDiAttesaSuArco = 0;
		//ora in cui il veicolo entra in un arco, utile per calcolare la posizione sull'arco
//		startStopTime = 0;
		
	//parametri statistici
	protected double
		//tempo di attesa ai semafori
		tempoDiAttesaTotale = 0,
		//ora di ingresso del veicolo nel grafo
		startTime,
		//indica il numero di nodi attraversati (per il calcolo del tempo medio di attesa ai semafori)
		numNodiAttraversati = 0,
		//indica la distanza percorsa
		distanceTraveled = 0; //meter
	
	//////////////////////////
	
	
	// COSTR /////////////////////////////////
	public Vehicle(){}
	public Vehicle(CityGraph graph, String name) {
		super(graph, name);

	}
	
	//////////////////////////////////////////
	
	/// GETTER //////////////
//	public StatVeicolo getStatVeicolo() {return stat;}
	public double getDistanceTraveled(){return distanceTraveled;}
	public boolean isMoving(){return moving;}
	public double getVelocity(){return Param.velocitaVeicolo;}
	public NetNode getStartingNode(){return startingNode;}
	public NetNode getCurrentNode() {return currentNode;}
	public NetNode getNextNode(){return nextNode;}
	public NetNode getTargetNode(){return destinationNode;}
	public NetEdge getCurrentEdge(){return currentEdge;}
	public double getStartTime() {return startTime;}
	public double getTempoDiAttesa() {return tempoDiAttesaTotale;}
	public double getNumeroNodiAttraversati() {return numNodiAttraversati;}
	
	// SETTER ///////////////
	public Vehicle setStartingNode(int index){
		/*print*
		System.out.println(this+": set nodo iniziale = "+getGraph().getNode(index));
		/**/
		startingNode = getGraph().getNode(index);
		setCurrentNode(index);
		
		return this;
	
	}
	public Vehicle setStartingNode(String node){
		/*print*
		System.out.println(this+": set nodo iniziale = "+node);
		/**/
		startingNode = getGraph().getNode(node);
		setCurrentNode(node);

		return this;
	
	}
	public Vehicle setCurrentNode(int index){
		currentNode = getGraph().getNode(index);
		return this;
	
	}
	public Vehicle setCurrentNode(String node){
		currentNode = getGraph().getNode(node);
		return this;
	
	}
	public Vehicle setNextNode(String node){
		nextNode = getGraph().getNode(node);
		return this;
	
	}
	public Vehicle setTargetNode(int index){
		destinationNode = getGraph().getNode(index);
		return this;
	
	}
	public Vehicle setTargetNode(String node){
		destinationNode = getGraph().getNode(node);
		return this;
	
	}
	
	
	
	// METHODS ////////////////////////////
	@Deprecated
	private NetNode plsGiveMeThe(NetNode nodeInTheEdge){
		// i don't know why but the node in the edges haven't attributes 
		String id = nodeInTheEdge.getId();
		return getGraph().getNode(id);
		
	}
	
	public double  getPositionOnTheEdge(){
		return position*((Double)currentEdge.getAttribute("length"));
	}
	
	private boolean ciSonoVeicoliDavanti() {
		double onTheEdge = currentEdge.getAttribute("length");
		double distanceFromVehicle;
		
		//non posso muovermi se sono troppo vicino al prossimo veicolo
		for(Vehicle v : getGraph().getVehiclesOnTheEdge(currentEdge.getId())){
			//distanza tra i veicoli
			distanceFromVehicle = (v.position*onTheEdge) - (this.position*onTheEdge);

			//se il veicolo analizzato è dietro di me, analizza il prossimo veicolo
			if(distanceFromVehicle<=0 || v==this){continue;}
			
			//altrimenti
			else if(distanceFromVehicle <= Param.distanzaInterveicolo){
                /*print*
                if(getId().equals("1")) {
            	System.out.println();
            	System.out.println(this+": non posso muovermi perchè sono troppo vicino a "+v);
                }
                /**/

				return true;
			}
		}
		return false;
		
	}
	
	
	@Override
	public void move() {
		if(fermoAlPrimoNodo) {
//			stat = new StatVeicolo(this);

			/*print*
			System.out.println();
			System.out.println(this+": sono fermo al primo nodo");
			/**/
			
			//se è stato assegnato un percorso al veicolo
			if(path.size() > 0) {
				//controllo che l'arco non sia pieno
				fermoAlPrimoNodo = ( (path.getFirst() != null) && getGraph().edgeIsFull(path.getFirst()) );
				
				/*print*
				System.out.println(this+": sono ancora fermo al primo nodo? "+fermoAlPrimoNodo);
				/**/
				
				//se l'arco è pieno non mi muovo
				if(fermoAlPrimoNodo)return;
				
				//altrimenti inizio a muovermi
				
				//tempo in cui il veicolo inizia a muoversi per la prima volta
				startTime = getScheduler().getCurrentTime();
				
				//tempo di ingresso sull'arco
				ingressTimeOnCurrentEdge = startTime;
				tempoDiAttesaSuArco = 0;
				
				//assegno il primo arco
				currentEdge = path.removeFirst();
				
				currentNode = currentEdge.getSourceNode();
				nextNode = currentEdge.getTargetNode();
				
			
				distanceTraveled += (double)currentEdge.getAttribute("length");
				attachToEdge(currentEdge.getId());
				
				//comunico al primo RSU che inizio a muovermi
//				Message cambioArco = new Message("CAMBIO ARCO", this, registeredRSU, Param.elaborationTime);
//				cambioArco.setData(null, nextNode);
//				sendEvent(cambioArco);

				/*print*
				System.out.println(this+": inizio a muovermi per la prima volta sull'arco "+currentEdge+" a "+startTime);
				/**/
				
			}
			
			//se non è stato assegnato un percorso al veicolo sono ancora fermo al primo nodo
			else return;
		}

		//se non posso muovermi
		if(ciSonoVeicoliDavanti()) {
			//conto il tempo di attesa sull'arco
			tempoDiAttesaSuArco += Param.updatePositionTime;
		
			/*print*
			System.out.println(this+": sono fermo dietro ad un veicolo");
			/**/
		
			return;
		}
		//se posso muovermi
		else {
			//se sono a fine arco
			if(position >= 1) {
				//se sono arrivato a destinazione
				if(path.size() <=0 || path.getFirst() == null) {
					
					//comunico che il veicolo ha abbandonato l'arco
					Message cambioArco = new Message("CAMBIO ARCO", this, registeredRSU, Param.elaborationTime);
					cambioArco.setData(currentEdge.getSourceNode(), null);
					sendEvent(cambioArco);

					
					//rimuovo il veicolo dal grafo
					getGraph().rimuoviVeicolo(this);
					addAttribute("ui.style", "fill-color: rgba(0,0,0,100);");
					addAttribute("ui.label", "");
					
					//aggiorno i parametri statistici
					tempoDiAttesaTotale += tempoDiAttesaSuArco;
					numNodiAttraversati++;

					//aggiorno le statistiche del veicolo
//					stat = new StatVeicolo(this);
					//aggiorno le statistiche del semaforo
//					((RSU)registeredRSU).getStat().aggiornaTempoMedioDiAttesa(tempoDiAttesaSuArco);
					//aggiorno le statistiche generali
//					stat.update();
//					StatisticheVeicoli statisticheVeicoli = (StatisticheVeicoli)(getGraph().getStatistiche().getStat(StatType.VEICOLI));
//					statisticheVeicoli.addStatVeicolo(stat);
					
					/*print*
					System.out.println("\n"+this+": sono arrivato a destinazione, tempo di attesa totale = "+tempoDiAttesaTotale);
					/**/
					return;
				}

				//se il semaforo è rosso resto fermo
				if(!verdeAlSemaforo) {
					tempoDiAttesaSuArco += Param.updatePositionTime;
					
					/*print*
					if(getId().equals("5")){
						System.out.println();
						System.out.println(this+": sono a FINE ARCO ma il semaforo è ROSSO");
					}/**/
					
					return;
				}

				//se il prossimo arco è pieno resto fermo
				if(getGraph().edgeIsFull(path.getFirst())) {
					tempoDiAttesaSuArco += Param.updatePositionTime;
					
					/*print*
					if(getId().equals("5")){
					System.out.println();
					System.out.println(this+": sono a FINE ARCO ma l'arco "+path.getFirst()+" è PIENO");
					}/**/
					return;
				}
				
				
				//cambio arco
				NetNode nodoProvenienza = currentEdge.getSourceNode();
				
				currentEdge = path.removeFirst();
				currentNode = currentEdge.getSourceNode();
				
				nextNode = currentEdge.getTargetNode();
				
				NetNode nodoSuccessuvo = nextNode;
				
				//comunico il cambio arco all'RSU
				Message cambioArco = new Message("CAMBIO ARCO", this, registeredRSU, Param.elaborationTime);
				cambioArco.setData(nodoProvenienza, nodoSuccessuvo);
				sendEvent(cambioArco);

				
				//aggiorno le statistiche semafori
//				((RSU)registeredRSU).getStat().aggiornaTempoMedioDiAttesa(tempoDiAttesaSuArco);

				//aggiorno i parametri
				ingressTimeOnCurrentEdge = getScheduler().getCurrentTime();
				tempoDiAttesaTotale += tempoDiAttesaSuArco;
				tempoDiAttesaSuArco = 0;
				distanceTraveled += (double)currentEdge.getAttribute("length");
				numNodiAttraversati++;
				attachToEdge(currentEdge.getId());
				
				/*print*
				if(getId().equals("5")){
				System.out.println(this+": sono in mezzo l'ARCO "+currentEdge);
				System.out.println(this+": tempo di attesa totale = "+tempoDiAttesaTotale);
				}
			/**/
			}
			
			//aggiorno posizione sull'arco
			position = relativePosition();
			setPosition(position);
			
			
			
		}
		
				
	}
	
	public double getY(){
		if(getNextNode() == null){return Double.MAX_VALUE;}
		
		//x,y position next node
		double x1 = plsGiveMeThe(nextNode).getAttribute("x");
		double y1 = plsGiveMeThe(nextNode).getAttribute("y");
		//x,y position prev node
		double x0 = plsGiveMeThe(currentNode).getAttribute("x");
		double y0 = plsGiveMeThe(currentNode).getAttribute("y");
		//x,y
		double x = x1-x0;
		double y = y1-y0;
		//alfa <
		double alfa = Math.atan2(y,x);
		
		double onTheEdge = currentEdge.getAttribute("length");
		
		double ipotenusa = position * onTheEdge; //Ù©(â—�Ì®Ì®Ìƒâ€¢)Û¶
		
		return y0 + (ipotenusa * Math.sin(alfa)); 	
	
	}
	public double getX(){
		if(getNextNode() == null){return Double.POSITIVE_INFINITY;}
		
		//x,y position next node
		double x1 = plsGiveMeThe(nextNode).getAttribute("x");
		double y1 = plsGiveMeThe(nextNode).getAttribute("y");
		//x,y position prev node
		double x0 = plsGiveMeThe(currentNode).getAttribute("x");
		double y0 = plsGiveMeThe(currentNode).getAttribute("y");
		//x,y
		double x = x1-x0;
		double y = y1-y0;
		//alfa <
		double alfa = Math.atan2(y, x);
		
		double onTheEdge = currentEdge.getAttribute("length");
		
		double ipotenusa = position*onTheEdge; //Ù©(â—�Ì®Ì®Ìƒâ€¢)Û¶
		
		return x0 + (ipotenusa * Math.cos(alfa));
		
	}
	public double relativePosition(){
		double timeOnTheEdge = getScheduler().getCurrentTime() - ingressTimeOnCurrentEdge - tempoDiAttesaSuArco;
		
		//change velocity to km/h in m/ms
		// K km/h = [K * (10^3 /3.600.000)] m/ms
		double newVelocity = Param.velocitaVeicolo;
		
		//relative position on the Edge
		double distanceFromSourceNode = newVelocity*timeOnTheEdge;
		double edgeLength = currentEdge.getAttribute("length");
				
		return distanceFromSourceNode/edgeLength;

	}
	public void getPathTo(NetNode destination){
		path.clear();
		
		/*print*
		System.out.println("\n"+this+": invio richiesta percorso a "+currentNode +" per la destinazione "+destination);
		/**/
		
		Message askPath = new Message("RICHIESTA PERCORSO", this, currentNode, Param.elaborationTime); 
		
		askPath.setData(destinationNode);
		sendEvent(askPath);

	}
	
//	public StatVeicolo getStat(){return stat;}
	
	
	///////////////////////////////////////
	///// OVERRIDE ////////////////////////
	///////////////////////////////////////
	// from comparable
		public int compareTo(Vehicle v){
			double pippopolino = getPositionOnTheEdge() - v.getPositionOnTheEdge();
			if(pippopolino < 0){
				return 1;
			}else if(pippopolino > 0){
				return -1;
			}
			return 0;
		}
	// from sprite
	@Override
	public void setPosition(double percent) {
		/* * 
		if(position>1)
		System.out.println("PORCODDio");
		/**/
		setPosition(Units.PX, percent, Param.distanceFromTheEdge, 0);

	}
	@Override
	public void attachToEdge(String id){
		detach();
		getGraph().attachVehicleToEdge(id,this);
		setPosition(Units.PX, 0, Param.distanceFromTheEdge, 0);
		
		super.attachToEdge(id);

	}
	@Override 
	public void detach(){
		NetEdge attachment = (NetEdge)getAttachment();
		if(attachment != null){
			String idEdge = attachment.getId();
			getGraph().removeVehicleFromEdge(idEdge, this);
		}
		super.detach();
	
	}

}