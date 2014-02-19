package bank.u01.socket;

import java.io.IOException;

import bank.Bank;
import bank.BankDriver;

public class Driver implements BankDriver {

	private Bank bank;

	@Override
	public void connect(String[] args) throws IOException {
		bank = new SocketBank(args[0], Integer.parseInt(args[1]));
	}

	@Override
	public void disconnect() throws IOException {
		//bye (stateles protocol)
	}

	@Override
	public Bank getBank() {
		return bank;
	}

}
