package bank.u06.jms;

import java.io.IOException;

import javax.naming.NamingException;

import bank.Bank;
import bank.BankDriver2;
import bank.u01.socket.protocol.SocketUtil;

public class Driver implements BankDriver2 {

	private JMSBank bank;
	@Override
	public void connect(String[] args) throws IOException {
		try {
			bank = new JMSBank();
		} catch (NamingException e) {
			throw new IOException(e);
		}
		SocketUtil.registerCommands();//TODO: Specify!
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
		
	}

}
