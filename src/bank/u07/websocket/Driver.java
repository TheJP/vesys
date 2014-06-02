package bank.u07.websocket;

import java.io.IOException;

import javax.naming.NamingException;

import bank.Bank;
import bank.BankDriver;
import bank.BankDriver2;
import bank.u01.socket.protocol.SocketUtil;

public class Driver implements BankDriver {

	private WebsocketBank bank;
	@Override
	public void connect(String[] args) throws IOException {
		bank = new WebsocketBank();
		SocketUtil.registerCommands(bank);
	}

	@Override
	public void disconnect() throws IOException {
		bank = null;
	}

	@Override
	public Bank getBank() {
		return bank;
	}

//	@Override
//	public void registerUpdateHandler(UpdateHandler handler) throws IOException {
//	}

}
