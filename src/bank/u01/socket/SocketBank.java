package bank.u01.socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Set;

import bank.Account;
import bank.Bank;
import bank.InactiveException;
import bank.OverdrawException;
import bank.u01.socket.protocol.CreateAccountCommand;
import bank.u01.socket.protocol.SocketCommand;

public class SocketBank implements Bank {

	private String address;
	private int port;

	public SocketBank(String address, int port) {
		this.address = address;
		this.port = port;
	}

	/**
	 * Sends the command over the Socket connection
	 * @param outputCmd
	 * @return
	 * @throws Exception
	 */
	protected SocketCommand sendCommand(SocketCommand outputCmd) throws IOException{
		SocketCommand inputCmd = null;
		Socket clientSocket = null;
		try {
			clientSocket = new Socket(address, port);
			DataOutputStream outputStream = new DataOutputStream(clientSocket.getOutputStream());
			DataInputStream inputStream = new DataInputStream(clientSocket.getInputStream());
			outputCmd.send(outputStream);
			inputCmd = SocketCommand.createCommand(inputStream);
		} catch (Exception e) {
			if(e instanceof IOException){ throw e; }
			e.printStackTrace();
		} finally {
			if(clientSocket != null){ clientSocket.close(); }
		}
		if(inputCmd == null){ throw new IOException("Unkown result"); }
		return inputCmd;
	}

	@Override
	public String createAccount(String owner) throws IOException {
		CreateAccountCommand outputCmd = new CreateAccountCommand(owner);
		SocketCommand inputCmd = sendCommand(outputCmd);
		return inputCmd.getType();
	}

	@Override
	public boolean closeAccount(String number) throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Set<String> getAccountNumbers() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Account getAccount(String number) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void transfer(Account a, Account b, double amount)
			throws IOException, IllegalArgumentException, OverdrawException,
			InactiveException {
		// TODO Auto-generated method stub
		
	}

}