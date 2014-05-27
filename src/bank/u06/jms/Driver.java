package bank.u06.jms;

import java.io.IOException;

import javax.naming.NamingException;

import bank.Bank;
import bank.BankDriver2;
import bank.u01.socket.protocol.SocketUtil;

public class Driver implements BankDriver2 {

	private JMSBank bank;
	private JMSUpdateHandler handler;
	@Override
	public void connect(String[] args) throws IOException {
		try {
			bank = new JMSBank();
			SocketUtil.registerCommands(bank);
			handler = new JMSUpdateHandler();
			handler.start();
		} catch (NamingException e) {
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
		this.handler.registerUpdateHandler(handler);
	}

}
