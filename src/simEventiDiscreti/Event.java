package simEventiDiscreti;

public abstract class Event implements Comparable<Event> {

	// name of the event
	String name = null;
	// tra quanti millisecondi parte l'evento
	Double shiftTime = null;

	/////////////////////////////////////////////////

	public Event(String eventName, double amongManyMillisec) {
		name = eventName;
		shiftTime = amongManyMillisec;

	}

	// azione da compiere dopo l'evento
	public abstract void action();

	// set shift time
	public void shift(double millisec) {
		/*
		 * print* if(name.equals("UPDATE POSITION")) {
		 * System.out.println("Event: evento shiftato a "+millisec); } /
		 **/
		shiftTime = millisec;

	}

	public double getTime() {
		return shiftTime;
	}

	public String getName() {
		return name;
	}

	@Override
	public int compareTo(Event event) {
		double res = shiftTime - event.shiftTime;
		if (res < 0) return -1;
		if (res > 0) return 1;
		return 0;
	}
}
