package ITS.semafori;

import network.message.Message;

public interface MessageManager {
	static enum Type{
		SemaforoClassico,
		SemaforoIntelligente,
	}
	
	static MessageManager getMessageManager(Type type){
		return null;
	}
	
	
	void init();
	void readMessage(Message message);
}
