package bank.u01.socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import bank.Bank;
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
			switch (inputCmd.getType()) {
				case EchoCommand.TYPE:
					outputCmd = new EchoCommand(((EchoCommand)inputCmd).getText());
					break;
				default:
					outputCmd = new EchoCommand("notfound");
					break;
			}
			outputCmd.write(outputStream);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
