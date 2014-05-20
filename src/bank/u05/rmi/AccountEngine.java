package bank.u05.rmi;

import bank.InactiveException;
import bank.OverdrawException;
import bank.local.LocalAccount;

public class AccountEngine extends LocalAccount implements RemoteAccount {

	private static final long serialVersionUID = -395193009633115665L;
	private IUpdateable updater;

	public AccountEngine(String owner, IUpdateable updater) {
		super(owner);
		this.updater = updater;
	}

	@Override
	public void deposit(double amount) throws InactiveException {
		super.deposit(amount);
		updater.update(getNumber());
	}

	@Override
	public void withdraw(double amount) throws InactiveException, OverdrawException {
		super.withdraw(amount);
		updater.update(getNumber());
	}
}
