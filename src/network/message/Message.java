package network.message;

import java.io.Serializable;
import simEventiDiscreti.Entity;
import simEventiDiscreti.Event;

public class Message extends Event implements Serializable {

	private static final long serialVersionUID = 1L;
	private Object[] data;
	private int TTL;

	private Entity source, destination;

	public Message(String messageName, Entity source, Entity destination, double amongManyMillisec) {
		super(messageName, amongManyMillisec);

		this.source = source;
		this.destination = destination;

	}

	public void setData(Object... data) {
		this.data = data;
	}

	public void setTTL(int ttl) {
		TTL = ttl;
	}

	public Object[] getData() {
		return data;
	}

	public Entity getSource() {
		return source;
	}

	public Entity getDestination() {
		return destination;
	}

	public int getTTL() {
		return TTL;
	}

	public void action() {
		destination.handler(this);

	}

}
