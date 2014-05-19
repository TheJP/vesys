package bank.u05.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

import bank.Account;

public interface RemoteAccount extends Account, Remote {
	void setActive(boolean active) throws RemoteException;
}
