package serverStatistiche;

import java.rmi.Remote;
import java.rmi.RemoteException;
import ITS.RSU.RSU;
import ITS.RSU.RemoteRSU;

public interface ServerStatistiche extends Remote {

	
	void updateStatistiche(String nomeVariabile, double quantitaMes, double quantitaAuto ) throws RemoteException;
	
	void registraRSU(RemoteRSU rsu)throws RemoteException;
}
