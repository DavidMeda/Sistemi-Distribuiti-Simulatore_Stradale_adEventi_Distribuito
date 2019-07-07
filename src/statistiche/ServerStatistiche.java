package statistiche;

import java.rmi.Remote;
import java.rmi.RemoteException;
import ITS.RSU.RSU;
import ITS.RSU.RemoteRSU;

public interface ServerStatistiche extends Remote {

	void updateStatistiche(RemoteRSU idRSU, Variabile var) throws RemoteException;

	void registraRSU(RemoteRSU rsu) throws RemoteException;

	public void richiestaStatisticheGenerali() throws RemoteException;
}
