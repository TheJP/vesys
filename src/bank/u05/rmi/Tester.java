package bank.u05.rmi;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Set;

public class Tester {

	public static void main(String[] args) {
		try {
			Registry registry = LocateRegistry.getRegistry(1099);
			RemoteBank bank = (RemoteBank) registry.lookup("Bank");
			String number = bank.createAccount("JP");
			System.out.println("Created: " + number);
			Set<String> accounts = bank.getAccountNumbers();
			for(String account : accounts){
				System.out.println("a " + account + " Owner: " + bank.getAccount(account).getOwner());
			}
		} catch (NotBoundException | IOException e) {
			e.printStackTrace();
		}
		
	}

}
