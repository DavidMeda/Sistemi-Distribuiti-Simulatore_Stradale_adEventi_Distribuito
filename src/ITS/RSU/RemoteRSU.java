package ITS.RSU;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteRSU extends Remote {
	
	void stampaStatistiche(double statistica) throws RemoteException;

}
