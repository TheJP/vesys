package bank.u05.rmi;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import bank.local.LocalBank;

public class BankEngine extends LocalBank implements RemoteBank {
	public static void main(String[] args){
		try {
			BankEngine engine = new BankEngine();
			RemoteBank bank = (RemoteBank) UnicastRemoteObject.exportObject(engine, 0);
			Registry registry;
			try {
				System.out.println("Creating Registry");
				registry = LocateRegistry.createRegistry(1099);
			} catch(RemoteException e) {
				System.err.println("Could not create Registry. Trying to connect to existing");
				registry = LocateRegistry.getRegistry();
				System.err.println("Worked");
			}
			System.out.println("Rebind Bank");
			registry.rebind("Bank", bank);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
}
