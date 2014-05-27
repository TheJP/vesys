package bank.u06.jms;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.Topic;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import bank.Bank;
import bank.BankBase;
import bank.local.LocalBank;
import bank.u01.socket.protocol.SocketUtil;

public class JMSServer implements Runnable {

	private Bank localBank;
	private Thread acceptorThread;
	private ExecutorService executors;
	private boolean running = false;
	//JMS
	private Context jndiContext;
	private ConnectionFactory factory;
	private Queue queue;
	private Topic topic;

	public JMSServer(Bank localBank) throws NamingException{
		this.localBank = localBank;
		acceptorThread = new Thread(this);
		executors = Executors.newFixedThreadPool(20);
		//JMS
		jndiContext = new InitialContext();
		factory = (ConnectionFactory) jndiContext.lookup("ConnectionFactory");
		queue = (Queue) jndiContext.lookup("BANK");
		topic = (Topic) jndiContext.lookup("BANK.LISTENER");
	}

	/**
	 * Start the Server
	 * @throws IOException
	 */
	public void start() throws IOException {
		acceptorThread.start();
		running  = true;
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
		try (JMSContext context = factory.createContext()) {
			JMSConsumer consumer = context.createConsumer(queue);
			JMSProducer sender = context.createProducer();
			//Connection accepting code
			while(running){
			    Message request = consumer.receive();
				try {
				    System.out.println("Handle: " + request.getBody(byte[].class));
					sender.send(request.getJMSReplyTo(), "Echo: " + request.getBody(byte[].class));
				} catch (JMSException e) { e.printStackTrace(); }
			}
		}
		//Termination Code
		try{
			executors.shutdown();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		SocketUtil.registerCommands();
		Scanner scanner = new Scanner(System.in);
		//Start the Server
		BankBase b = new LocalBank();
		try {
			JMSServer server = new JMSServer(b);
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
		} catch (NamingException e1) {
			e1.printStackTrace();
		}
	}
}
