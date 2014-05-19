package bank.u05.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

import bank.Bank;

public interface RemoteBank extends Remote, Bank {
	void registerUpdateHandler(RemoteUpdate ru) throws RemoteException;
}
