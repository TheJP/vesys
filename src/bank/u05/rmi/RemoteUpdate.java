package bank.u05.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteUpdate extends Remote {
	void update(String accountNr) throws RemoteException;
}
