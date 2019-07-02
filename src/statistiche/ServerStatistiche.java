package statistiche;

import java.rmi.Remote;
import java.rmi.RemoteException;
import ITS.RSU.RSU;
import ITS.RSU.RemoteRSU;

public interface ServerStatistiche extends Remote {

	void updateStatistiche(RemoteRSU idRSU, Variabili var) throws RemoteException;

	void registraRSU(RemoteRSU rsu) throws RemoteException;

	public String statisticheGenerali() throws RemoteException;
}
