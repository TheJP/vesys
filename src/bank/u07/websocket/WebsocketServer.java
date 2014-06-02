package bank.u07.websocket;

import java.util.Scanner;

import javax.naming.NamingException;
import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.glassfish.tyrus.server.Server;

import bank.BankBase;
import bank.local.LocalBank;
import bank.u01.socket.protocol.SocketUtil;

@ServerEndpoint(value = "/bank")
public class WebsocketServer {

	private BankBase localBank;

    @OnMessage
    public String onMessage(String message, Session session) {
        return message;
    }

	public WebsocketServer() throws NamingException{
		this.localBank = new LocalBank();
	}

	public static void main(String[] args) {
		SocketUtil.registerCommands();
		Scanner scanner = new Scanner(System.in);
		//WebsocketServer s = new WebsocketServer(b);
		Server server = new Server("localhost", 8025, "/bank", WebsocketServer.class);
		try {
			server.start();
			//Accept user interaction with Server
			String line = "";
			do {
				line = scanner.nextLine();
				switch(line){
					case "help":
					case "?":
						System.out.println("Shutdown Server with q or stop");
						break;
				}
			} while(!line.equals("q") && !line.equals("stop"));
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			//Stop the Server
			server.stop();
			scanner.close();
			System.exit(0);
		}
	}
/*
	@Override
	public void update(String number) {
		try (JMSContext context = factory.createContext()) {
			JMSProducer publisher = context.createProducer();
			publisher.send(topic, number);
		}
	}
*/
}
