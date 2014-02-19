package bank.u01.socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

import bank.u01.socket.protocol.EchoCommand;
import bank.u01.socket.protocol.SocketCommand;
import bank.u01.socket.protocol.SocketUtil;

public class EchoTestClient {

	public static void main(String[] args) {
		SocketUtil.registerCommands();
		Scanner scanner = new Scanner(System.in);
		String line;
		do {
			line = scanner.nextLine();
			try {
				EchoCommand outputCmd = new EchoCommand(line);
				Socket clientSocket = new Socket(InetAddress.getLoopbackAddress(), 55555);
				DataOutputStream outputStream = new DataOutputStream(clientSocket.getOutputStream());
				DataInputStream inputStream = new DataInputStream(clientSocket.getInputStream());
				outputCmd.send(outputStream);
				SocketCommand inputCmd = SocketCommand.createCommand(inputStream);
				if(inputCmd instanceof EchoCommand){
					System.out.println(((EchoCommand)inputCmd).getText());
				}
				clientSocket.close();
			}
			catch (Exception ex){ }
		} while(line != "");
		scanner.close();
	}

}
