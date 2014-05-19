package bank.u05.rmi;

import java.io.IOException;
import java.rmi.RemoteException;

import bank.BankDriver2;
import bank.BankDriver2.UpdateHandler;

public class UpdateEngine implements RemoteUpdate {

	private BankDriver2.UpdateHandler handler;
	public UpdateEngine(UpdateHandler handler) {
		this.handler = handler;
	}

	@Override
	public void update(String accountNr) throws RemoteException {
		try {
			handler.accountChanged(accountNr);
		} catch (IOException e) {
			throw new RemoteException(e.getMessage());
		}
	}

}
