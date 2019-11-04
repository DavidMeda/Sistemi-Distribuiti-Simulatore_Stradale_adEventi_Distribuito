package ITS.semafori;

import network.NetEdge;

public interface Semaforo {

	double setVerde(); // restituisce anche la durata del verde

	void setRosso();

	void sempreVerde();

	NetEdge getEdge();
}
