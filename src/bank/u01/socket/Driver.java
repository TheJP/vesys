package bank.u01.socket;

import java.io.IOException;

import bank.Bank;
import bank.BankDriver;

/**
 * Implementation of the driver for the socket client
 * @author JP
 *
 */
public class Driver implements BankDriver {

	private Bank bank;

	@Override
	public void connect(String[] args) throws IOException {
		bank = new SocketBank(args[0], Integer.parseInt(args[1]));
	}

	@Override
	public void disconnect() throws IOException {
		//bye bye (stateless protocol, no disconnect needed)
	}

	@Override
	public Bank getBank() {
		return bank;
	}

}
