package simEventiDiscreti;

import java.util.LinkedList;
import java.util.PriorityQueue;

import util.Param;

public class Scheduler extends Thread{
	@SuppressWarnings("unused")
	private static final int 
		MAX_TIMEHORIZON_24DAY = Integer.MAX_VALUE;
		/*TODO time unit
		SEC = 1000,
		MIN = 60000;
		*/
	private boolean 
		realTime = 
//			false,
			true,
		start = false;
	
	private double 
		timeHorizon = 0,
		currentTime = 0;
	
	private PriorityQueue<Event> queue = new PriorityQueue<>();
	private LinkedList<Event> story = new LinkedList<>();
		
	int cont = 0;
	
	
	// COSTR //////////////////////////////////////////
	public Scheduler(){}
	public Scheduler(double timeHorizonMillisec){timeHorizon = timeHorizonMillisec;}
	
	// GETTER //////////////////////////////////////////
	public PriorityQueue<Event> getEventQueue(){return queue;}
	public LinkedList<Event> getStoryQueue(){return story;}
	public boolean getStart() {return start;}
	
	// METHODS ////////////////////////////////////
	public void reset() {
		queue.clear();
		story.clear();
		currentTime = 0;
	}
	public synchronized void addEvent(Event event){
		/*print*
		if(event.getName().equals("CAMBIO FASE")) {
			System.out.println("Scheduler: addEvent. "+(event.getName()));
			System.out.println("---------- current time = "+currentTime);
			System.out.println("---------- event time = "+event.getTime());
//			System.out.println("---------- action at "+(currentTime + event.getTime()));
			System.out.println("---------- contatore = "+(cont++));
		}
		/**/

		event.shift(currentTime + event.getTime());
		queue.add(event);
		
		
	}
	
	/////////////////
	public void stopSimulation(){
		start = false;
		queue.clear();
		
	}
	//////////////////
	public void run(){
		start = true;
		Event event = null;
		
		while(start){
			if(queue.size()>0){
				//get next event
				event = queue.remove();
				/*TODO*/
				if(realTime) {
					
					try {
						if(event.getName().equals("UPDATE POSITION"))
							Thread.sleep((long)Param.updatePositionTime);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				/**/

				/*print*
				if(event.getName().equals("UPDATE POSITION")) {
					System.out.println("\nScheduler: aggiorno current time da "+currentTime+" a "+event.getTime());
				}
				/**/

				//refresh current time
				currentTime = event.getTime();
				
				//action on the event
				event.action();
				/*print*
				if(!event.getName().equals("UPDATE POSITION") &&
						!event.getName().equals("PING")) {
				System.out.println("SCHEDULER - ACTION SU "+event.getName()+" a "+event.getTime());
//					System.out.println("---- messaggio inviato da "+((Message)event).getSource());
				}
//				if(event.getName().equals("CAMBIO ARCO")){
//				}
				/**/
				
				//stop simulation if time horizon is end
				if(timeHorizon > 0 && currentTime>timeHorizon){
					start = false;
				}
				
			}
		}
		
		/*print*
		System.out.println("\n"+this+": SIMULAZIONE TERMINATA");
		/**/
	}
	// GETTER ////////////////////////////////
	public double getCurrentTime(){return currentTime;}
	
	/////////////////////// TODO ///////////////////////////////
	@Deprecated
	public void pause(){/*pause = true;*/}
	@Deprecated
	public void restart(){/*pause = false;*/}
	
	
	
	//////////////////////////////
	/////////////////////////////
	/* *
	public static void main(String[] args) {
		Scheduler s = new Scheduler();
		s.addEvent(new Event("forseNO", 10) {
			
			@Override
			public void action() {
				System.out.println(getName()+" "+getTime());
			}

		});
		s.start();
//		try {
//			Thread.sleep(2);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		s.addEvent(new Event("forseSI", 5) {
			
			@Override
			public void action() {
				System.out.println(getName()+" "+getTime());
//				s.stopSimulation();
			}
		});
		try {
			Thread.sleep(900);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		s.stopSimulation();
	}
	/**/
}
