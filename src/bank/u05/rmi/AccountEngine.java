package bank.u05.rmi;

import bank.local.LocalAccount;

public class AccountEngine extends LocalAccount implements RemoteAccount {

	private static final long serialVersionUID = -395193009633115665L;

	public AccountEngine(String owner) {
		super(owner);
	}
}
