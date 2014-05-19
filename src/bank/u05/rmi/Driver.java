package bank.u05.rmi;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import bank.Bank;
import bank.BankDriver2;

public class Driver implements BankDriver2 {

	private RemoteBank bank;

	@Override
	public void connect(String[] args) throws IOException {
		Registry registry = LocateRegistry.getRegistry(1099);
		try {
			bank = (RemoteBank) registry.lookup("Bank");
		} catch (NotBoundException e) {
			throw new IOException(e);
		}
		
	}

	@Override
	public void disconnect() throws IOException {
		bank = null;
	}

	@Override
	public Bank getBank() {
		return bank;
	}

	@Override
	public void registerUpdateHandler(UpdateHandler handler) throws IOException {
		UpdateEngine newEngine = new UpdateEngine(handler);
		UnicastRemoteObject.exportObject(newEngine, 0);
		bank.registerUpdateHandler(newEngine);
	}

}
