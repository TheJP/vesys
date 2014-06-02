package bank.u07.websocket;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.websocket.DeploymentException;

import org.glassfish.tyrus.client.ClientManager;

import bank.Bank;
import bank.BankDriver2;
import bank.u01.socket.protocol.SocketUtil;

public class Driver implements BankDriver2 {

	private WebsocketBank bank;
	@Override
	public void connect(String[] args) throws IOException {
		bank = new WebsocketBank();
		try {
			final URI url = new URI("ws://localhost:8025/bank/bank");
			ClientManager client = ClientManager.createClient();
			client.connectToServer(bank, url);
		} catch (URISyntaxException | DeploymentException e) { e.printStackTrace(); }
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

	@Override
	public void registerUpdateHandler(UpdateHandler handler) throws IOException {
	}

}
