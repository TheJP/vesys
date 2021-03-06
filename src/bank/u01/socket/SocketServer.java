package bank.u01.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import bank.BankBase;
import bank.local.LocalBank;
import bank.u01.socket.protocol.SocketUtil;

/**
 * 
 * @author JP
 *
 */
public class SocketServer implements Runnable {

	//Bank on which clients can run operations over this server
	protected BankBase localBank;
	//Thread executor for client handler
	private ExecutorService executors;
	//Server thread, which accepts connections and starts client handlers
	private Thread acceptorThread;
	//Used by the acceptor thread if the server should be shutdown
	private boolean running = false;
	//socket on the given port, which accepts client connections
	private ServerSocket serverSocket;

	//Port on which the Server will be started
	public final int SERVER_PORT = 55555;

	public SocketServer(BankBase localBank){
		this.localBank = localBank;
		acceptorThread = new Thread(this);
		executors = Executors.newFixedThreadPool(20);
	}

	/**
	 * Start the Server
	 * @throws IOException
	 */
	public void start() throws IOException {
		serverSocket = new ServerSocket(SERVER_PORT);
		acceptorThread.start();
		running = true;
	}
	
	/**
	 * Terminate the server
	 */
	public void stop(){
		running = false;
		acceptorThread.interrupt();
	}

	@Override
	public void run() {
		//Connection accepting code
		while(running){
			try {
				Socket socket = serverSocket.accept();
				if(running){
					executors.submit(new SocketServerHandler(socket, localBank));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		//Termination Code
		try{
			executors.shutdown();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try{
			serverSocket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		SocketUtil.registerCommands();
		Scanner scanner = new Scanner(System.in);
		//Start the Server
		BankBase b = new LocalBank();
		SocketServer server = new SocketServer(b);
		try {
			server.start();
			System.out.println("Server started");
			//Accept user interaction with Server
			String line = "";
			do {
				line = scanner.nextLine();
				switch(line){
					case "help":
					case "?":
						System.out.println("Shutdown Server with q or stop");
						break;
					case "print":
						for(String number : b.getAccountNumbers()){
							System.out.printf("%s ", number);
						}
						System.out.println();
						break;
				}
			} while(!line.equals("q") && !line.equals("stop"));
			//Stop the Server
			server.stop();
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			scanner.close();
		}
	}
}
