package simEventiDiscreti;

import java.util.PriorityQueue;
import util.Param;

public class Scheduler extends Thread {

	private boolean start = false;

	private double timeHorizon = 0, currentTime = 0;

	private PriorityQueue<Event> queue = new PriorityQueue<>();

	// COSTR //////////////////////////////////////////
	public Scheduler() {}

	public Scheduler(double timeHorizonMillisec) {
		timeHorizon = timeHorizonMillisec;
	}

	// GETTER //////////////////////////////////////////
	public PriorityQueue<Event> getEventQueue() {
		return queue;
	}

	public boolean getStart() {
		return start;
	}

	// METHODS ////////////////////////////////////
	public void reset() {
		queue.clear();
		currentTime = 0;
	}

	public synchronized void addEvent(Event event) {
		event.shift(currentTime + event.getTime());
		queue.add(event);

	}

	public void stopSimulation() {
		start = false;
		queue.clear();

	}

	public void run() {
		start = true;
		Event event = null;

		while (start) {
			if (queue.size() > 0) {
				// get next event
				event = queue.remove();

				try {
					if (event.getName().equals("UPDATE POSITION")) Thread.sleep((long) Param.updatePositionTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				/*
				 * print* if(event.getName().equals("UPDATE POSITION")) {
				 * System.out.println("\nScheduler: aggiorno current time da "+currentTime+" a "+event.
				 * getTime()); } /
				 **/

				// refresh current time
				currentTime = event.getTime();

				// action on the event
				event.action();
				/*
				 * print* if(!event.getName().equals("UPDATE POSITION") &&
				 * !event.getName().equals("PING")) {
				 * System.out.println("SCHEDULER - ACTION SU "+event.getName()+" a "+event.getTime()); //
				 * System.out.println("---- messaggio inviato da "+((Message)event).getSource()); } //
				 * if(event.getName().equals("CAMBIO ARCO")){ // } /
				 **/

//				// stop simulation if time horizon is end
//				if (timeHorizon > 0 && currentTime > timeHorizon) {
//					start = false;
//				}

			} else {
				start = false;

			}

		}

		/*
		 * print* System.out.println("\n"+this+": SIMULAZIONE TERMINATA"); /
		 **/
		// System.exit(-1);
	}

	// GETTER ////////////////////////////////
	public double getCurrentTime() {
		return currentTime;
	}
}
