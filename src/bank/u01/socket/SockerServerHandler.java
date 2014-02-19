package bank.u01.socket;

import java.net.Socket;

import bank.Bank;

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
		
	}
}
