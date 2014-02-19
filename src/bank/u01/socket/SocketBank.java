package bank.u01.socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Set;

import bank.Account;
import bank.Bank;
import bank.InactiveException;
import bank.OverdrawException;
import bank.u01.socket.protocol.*;

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
	@SuppressWarnings("unchecked")
	protected <T extends SocketCommand> T sendCommand(SocketCommand outputCmd) throws IOException{
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
		try { return (T) inputCmd; }
		catch(Exception e){ throw new IOException("Unkown result"); }
	}

	@Override
	public String createAccount(String owner) throws IOException {
		CreateAccountCommand outputCmd = new CreateAccountCommand(owner);
		CreatedAccountCommand inputCmd = sendCommand(outputCmd);
		return inputCmd.getValue();
	}

	@Override
	public boolean closeAccount(String number) throws IOException {
		CloseAccount outputCmd = new CloseAccount(number);
		ClosedAccount inputCmd = sendCommand(outputCmd);
		return inputCmd.getValue();
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