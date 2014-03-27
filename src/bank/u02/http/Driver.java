package bank.u02.http;

import java.io.IOException;

import bank.Bank;
import bank.BankDriver;

/**
 * Implementation of the driver for the http client
 * @author JP
 *
 */
public class Driver implements BankDriver {

	private Bank bank;

	@Override
	public void connect(String[] args) throws IOException {
		bank = new HttpBank(args[0], Integer.parseInt(args[1]));
	}

	@Override
	public void disconnect() throws IOException {
		bank = null;
	}

	@Override
	public Bank getBank() {
		return bank;
	}

}
