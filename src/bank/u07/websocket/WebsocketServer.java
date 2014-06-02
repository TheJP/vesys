package bank.u07.websocket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Scanner;
import java.util.Set;

import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.glassfish.tyrus.server.Server;

import bank.Account;
import bank.AccountBase;
import bank.BankBase;
import bank.InactiveException;
import bank.OverdrawException;
import bank.local.LocalBank;
import bank.u01.socket.protocol.AccountCommand;
import bank.u01.socket.protocol.AccountNumbersCommand;
import bank.u01.socket.protocol.CloseAccountCommand;
import bank.u01.socket.protocol.ClosedAccountCommand;
import bank.u01.socket.protocol.CreateAccountCommand;
import bank.u01.socket.protocol.CreatedAccountCommand;
import bank.u01.socket.protocol.DepositCommand;
import bank.u01.socket.protocol.EchoCommand;
import bank.u01.socket.protocol.GetAccountCommand;
import bank.u01.socket.protocol.GetAccountNumbersCommand;
import bank.u01.socket.protocol.SocketCommand;
import bank.u01.socket.protocol.SocketUtil;
import bank.u01.socket.protocol.StatusCommand;
import bank.u01.socket.protocol.StatusCommand.StatusId;
import bank.u01.socket.protocol.TransferCommand;
import bank.u01.socket.protocol.WithdrawCommand;

@ServerEndpoint(value = "/bank")
public class WebsocketServer {

	private static BankBase localBank;

	public WebsocketServer() { }

    @OnMessage
    public ByteBuffer onMessage(ByteBuffer message, Session session) throws IOException {
    	SocketCommand inputCmd = SocketCommand.fromBytes(message.array());
		try {
			SocketCommand outputCmd;
			String type = (inputCmd == null ? "" : inputCmd.getType());
			System.out.println(type);
			//Switch over the type String (requires java 7) to find correct implementation
			//(Most of the implementations speak for themselves and are not commented because of this)
			switch (type) {
				case EchoCommand.TYPE: //Echo is only used by the EchoTestClient client for the proof of concept
					outputCmd = new EchoCommand(((EchoCommand)inputCmd).getText());
					break;
				case CreateAccountCommand.TYPE:
					String accountNr = localBank.createAccount(((CreateAccountCommand)inputCmd).getValue());
					outputCmd = new CreatedAccountCommand(accountNr);
					update(accountNr, session);
					break;
				case CloseAccountCommand.TYPE:
					boolean success = localBank.closeAccount(((CloseAccountCommand)inputCmd).getValue());
					outputCmd = new ClosedAccountCommand(success);
					update(((CloseAccountCommand)inputCmd).getValue(), session);
					break;
				case GetAccountNumbersCommand.TYPE:
					Set<String> accountNumbers = localBank.getAccountNumbers();
					outputCmd = new AccountNumbersCommand(accountNumbers);
					break;
				case GetAccountCommand.TYPE:
					AccountBase account = localBank.getAccountBase(((GetAccountCommand)inputCmd).getValue());
					outputCmd = new AccountCommand(account);
					break;
				case TransferCommand.TYPE:
					TransferCommand tCmd = (TransferCommand)inputCmd;
					try {
						Account to = localBank.getAccount(tCmd.getTo());
						Account from = localBank.getAccount(tCmd.getFrom());
						localBank.transfer(from, to, tCmd.getValue());
						outputCmd = new StatusCommand();
						update(to.getNumber(), session);
						update(from.getNumber(), session);
					} catch(InactiveException ie){
						outputCmd = new StatusCommand(StatusId.InactiveException);
					} catch(IllegalArgumentException iae){
						outputCmd = new StatusCommand(StatusId.IllegalArgumentException);
					} catch(OverdrawException oe){
						outputCmd = new StatusCommand(StatusId.OverdrawException);
					}
					break;
				case DepositCommand.TYPE:
					DepositCommand dCmd = (DepositCommand)inputCmd;
					Account localAccount = null;
					try{
						localAccount = localBank.getAccount(dCmd.getAccountNr());
						localAccount.deposit(dCmd.getValue());
						outputCmd = new StatusCommand();
					} catch(InactiveException ie){
						outputCmd = new StatusCommand(StatusId.InactiveException);
					} catch(IllegalArgumentException iae){
						outputCmd = new StatusCommand(StatusId.IllegalArgumentException);
					}
					if(localAccount != null){ update(localAccount.getNumber(), session); }
					break;
				case WithdrawCommand.TYPE:
					WithdrawCommand wCmd = (WithdrawCommand)inputCmd;
					Account localAccount2 = null;
					try{
						localAccount2 = localBank.getAccount(wCmd.getAccountNr());
						localAccount2.withdraw(wCmd.getValue());
						outputCmd = new StatusCommand();
						update(localAccount2.getNumber(), session);
					} catch(InactiveException ie){
						outputCmd = new StatusCommand(StatusId.InactiveException);
					} catch(IllegalArgumentException iae){
						outputCmd = new StatusCommand(StatusId.IllegalArgumentException);
					} catch(OverdrawException oe){
						outputCmd = new StatusCommand(StatusId.OverdrawException);
					}
					if(localAccount2 != null){ update(localAccount2.getNumber(), session); }
					break;
				default:
					outputCmd = new StatusCommand(StatusId.IllegalArgumentException);
					break;
			}
			return ByteBuffer.wrap(outputCmd.toBytes());
		} catch (Exception e) { throw new IOException(e); }
    }

    /**
     * Sends an update notification to the client.
     */
	private void update(String accountNr, Session session) throws IOException {
		session.getBasicRemote().sendText(accountNr);
	}

	public static void main(String[] args) {
		localBank = new LocalBank();
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
