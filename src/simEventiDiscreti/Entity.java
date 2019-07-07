package simEventiDiscreti;

public interface Entity {
	//per evocare l'azione che deve compiere l'entità
	void handler(Event event);

	//aggiungi evento allo sheduler
	void sendEvent(Event event);
		
	Scheduler getScheduler();
}
