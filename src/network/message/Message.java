package network.message;

import ITS.RSU;
import network.NetEntity;
import simEventiDiscreti.Event;
import vanet.Vehicle;

public class Message extends Event { 
	private Object[] data;
	private int TTL;
	
	private NetEntity 
		source,
		destination;
	
	public Message(String messageName, NetEntity source, NetEntity destination, double amongManyMillisec) {
		super(messageName, amongManyMillisec);
		
		this.source = source;
		this.destination = destination;
		
		
	
	}
	
	
	public void setData(Object...data){this.data = data;}
	public void setTTL(int ttl){TTL = ttl;}
	
	
	public Object[] getData(){return data;}
	public NetEntity getSource(){return source;}
	public NetEntity getDestination(){return destination;}
	public int getTTL(){return TTL;}
	
	
	public void action() {
		destination.handler(this);
		if(destination != source && !getName().equals("START") && !getName().equals("CAMBIO_FASE")){
			
//			if(source instanceof Vehicle && destination instanceof RSU){
//				((Vehicle)source).getStat().updateMessTotali();
//				((Vehicle)source).getStat().updateMessInviatiVeicolo_RSU();
//				((RSU)destination).getStat().updateMessaggiRicevutiRSU_Veicolo();
//				((RSU)destination).getStat().updateMessaggiTotali();
//			}
//			
//			
//			if(source instanceof RSU &&  destination instanceof Vehicle){
//				((RSU)source).getStat().updateMessaggiTotali();
//				((RSU)source).getStat().updateMessaggiInviatiRSU_Veicolo();
//				((Vehicle)destination).getStat().updateMessTotali();
//				((Vehicle)destination).getStat().updateMessRicevutiVeicolo_RSU();
//				
//			}
//			if(source instanceof RSU &&  destination instanceof RSU ){
//				((RSU)source).getStat().updateMessaggiTotali();
//				((RSU)source).getStat().updateMessaggiTotali();
//				((RSU)source).getStat().updateMessaggiInviatiRSU_RSU();
//				((RSU)source).getStat().updateMessaggiRicevutiRSU_RSU();
//
//			}
		}
		
	}

}
