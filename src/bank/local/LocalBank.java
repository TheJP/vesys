package bank.local;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import bank.BankBase;
import bank.InactiveException;
import bank.OverdrawException;

/**
 * Local bank implementation. An implementation of bank, which allows to create
 * a working local bank which doesn't require a connection.
 * This implementation is mostly used by the different bank servers.
 * @author JP
 *
 */
public class LocalBank implements BankBase {

	/**
	 * Map in which all accounts are stored. Hashed by the account.nr
	 */
	private final Map<String, LocalAccount> accounts = new HashMap<>();

	@Override
	public Set<String> getAccountNumbers() {
		Set<String> activeAccounts = new HashSet<>();
		for(LocalAccount a : accounts.values()){
			if(a.isActive()){ activeAccounts.add(a.getNumber()); }
		}
		return activeAccounts;
	}

	@Override
	public String createAccount(String owner) {
		LocalAccount newAccount = new LocalAccount(owner);
		accounts.put(newAccount.getNumber(), newAccount);
		return newAccount.getNumber();
	}

	@Override
	public boolean closeAccount(String number) {
		if(accounts.containsKey(number)){
			return accounts.get(number).close();
		}else{
			return false;
		}
	}

	@Override
	public bank.Account getAccount(String number) {
		return accounts.get(number);
	}

	@Override
	public bank.AccountBase getAccountBase(String number) {
		return accounts.get(number);
	}

	@Override
	public void transfer(bank.Account from, bank.Account to, double amount)
			throws IOException, InactiveException, OverdrawException {
		//Start Transaction.. Not.. Would be nice though
		if(from.isActive() && to.isActive()){
			//No IOExceptions local!
			from.withdraw(amount); //Throws if amount < 0; Throws if amount > balance
			to.deposit(amount); //TODO: If to throws an Exception we are in an undefined state. What does guarantee, that we can deposit to from again?
		}else{
			throw new InactiveException();
		}
		//End Transaction
	}

}
