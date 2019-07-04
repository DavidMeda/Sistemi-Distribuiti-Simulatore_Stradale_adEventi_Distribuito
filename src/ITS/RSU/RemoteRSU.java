package ITS.RSU;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteRSU extends Remote {
	
	void stampaStatistiche(String statistica) throws RemoteException;
	int getID() throws RemoteException;
	String getNameRSU() throws RemoteException;

}
