package bank.u01.socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import bank.Bank;
import bank.u01.socket.protocol.CloseAccount;
import bank.u01.socket.protocol.ClosedAccount;
import bank.u01.socket.protocol.CreateAccountCommand;
import bank.u01.socket.protocol.CreatedAccountCommand;
import bank.u01.socket.protocol.EchoCommand;
import bank.u01.socket.protocol.SocketCommand;

/**
 * Handles a single client and stops afterwards
 * @author JP
 *
 */
public class SockerServerHandler implements Runnable {

	private Socket socket;
	private Bank localBank;
	public SockerServerHandler(Socket socket, Bank localBank){
		this.socket = socket;
		this.localBank = localBank;
	}

	@Override
	public void run() {
		try {
			DataInputStream inputStream = new DataInputStream(socket.getInputStream());
			DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
			SocketCommand inputCmd = SocketCommand.createCommand(inputStream);
			SocketCommand outputCmd;
			String type = (inputCmd == null ? "" : inputCmd.getType());
			switch (type) {
				case EchoCommand.TYPE:
					outputCmd = new EchoCommand(((EchoCommand)inputCmd).getText());
					break;
				case CreateAccountCommand.TYPE:
					String accountNr = localBank.createAccount(((CreateAccountCommand)inputCmd).getValue());
					outputCmd = new CreatedAccountCommand(accountNr);
				case CloseAccount.TYPE:
					boolean success = localBank.closeAccount(((CloseAccount)inputCmd).getValue());
					outputCmd = new ClosedAccount(success);
				default:
					outputCmd = new EchoCommand("notfound");
					break;
			}
			outputCmd.send(outputStream);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(!socket.isClosed()){ socket.close(); }
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
