package statistiche;

import java.rmi.Remote;
import java.rmi.RemoteException;
import ITS.RSU.RSU;
import ITS.RSU.RemoteRSU;

public interface ServerStatistiche extends Remote {

	void updateStatisticheRSU(RemoteRSU idRSU, VariabileStatRSU varRSU) throws RemoteException;
	
	void updateStatisticheVeicolo(VariabileStatVeicolo varVeicol) throws RemoteException;


	void registraRSU(RemoteRSU rsu) throws RemoteException;

	public void richiestaStatisticheGenerali() throws RemoteException;
}
