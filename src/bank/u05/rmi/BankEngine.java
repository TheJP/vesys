package bank.u05.rmi;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import bank.Account;
import bank.InactiveException;
import bank.OverdrawException;

public class BankEngine implements RemoteBank {

	/**
	 * Map in which all accounts are stored. Hashed by the account.nr
	 */
	protected final Map<String, RemoteAccount> accounts = new HashMap<>();

	@Override
	public Set<String> getAccountNumbers() throws IOException {
		Set<String> activeAccounts = new HashSet<>();
		for(Account a : accounts.values()){
			if(a.isActive()){ activeAccounts.add(a.getNumber()); }
		}
		return activeAccounts;
	}

	@Override
	public String createAccount(String owner) throws IOException {
		RemoteAccount newAccount = new AccountEngine(owner);
		/*newAccount = (RemoteAccount)*/ UnicastRemoteObject.exportObject(newAccount, 0);
		accounts.put(newAccount.getNumber(), newAccount);
		return newAccount.getNumber();
	}

	@Override
	public boolean closeAccount(String number) throws IOException {
		if(accounts.containsKey(number)){
			RemoteAccount a = accounts.get(number);
			if(a.getBalance() != 0.0){ return false; }
			if(!a.isActive()){ return false; }
			a.setActive(false);
			return true;
		}else{
			return false;
		}
	}

	@Override
	public bank.Account getAccount(String number) {
		return accounts.get(number);
	}

	@Override
	public void transfer(bank.Account from, bank.Account to, double amount)
			throws IOException, InactiveException, OverdrawException {
		//Start Transaction.. Not.. Would be nice though
		if(from.isActive() && to.isActive()){
			//No IOExceptions local!
			from.withdraw(amount); //Throws if amount < 0; Throws if amount > balance
			to.deposit(amount);
		}else{
			throw new InactiveException();
		}
		//End Transaction
	}

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
