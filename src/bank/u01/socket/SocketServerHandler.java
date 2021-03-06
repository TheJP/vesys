package bank.u01.socket;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Set;

import bank.Account;
import bank.AccountBase;
import bank.BankBase;
import bank.InactiveException;
import bank.OverdrawException;
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
import bank.u01.socket.protocol.StatusCommand;
import bank.u01.socket.protocol.StatusCommand.StatusId;
import bank.u01.socket.protocol.TransferCommand;
import bank.u01.socket.protocol.WithdrawCommand;

/**
 * Handles a single client and stops afterwards
 * @author JP
 *
 */
public class SocketServerHandler implements Runnable {

	/**
	 * Socket connection to the client of this handler
	 */
	private Socket socket;
	/**
	 * Local bank implementation which client request are executed on.
	 * (May also be a remote bank implementation)
	 */
	private BankBase localBank;
	public SocketServerHandler(Socket socket, BankBase localBank){
		this.socket = socket;
		this.localBank = localBank;
	}

	@Override
	public void run() {
		try {
			//TODO: 1. Create the interface ToServerCommand with a handle(Bank localBank) method
			//      2. Implement handle methods for the different commands
			//      3. Remove the switch statement and call the handle methods
			//** Main Server Handling Code **//
			//Use buffered streams for better performance
			DataInputStream inputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
			DataOutputStream outputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
			SocketCommand inputCmd = SocketCommand.createCommand(inputStream);
			SocketCommand outputCmd;
			String type = (inputCmd == null ? "" : inputCmd.getType());
			System.out.println(type); //Debug server output (will stay here because this is not productive code)
			//Switch over the type String (requires java 7) to find correct implementation
			//(Most of the implementations speak for themselfes and are not commented because of this)
			switch (type) {
				case EchoCommand.TYPE: //Echo is only used by the EchoTestClient client for the proof of concept
					outputCmd = new EchoCommand(((EchoCommand)inputCmd).getText());
					break;
				case CreateAccountCommand.TYPE:
					String accountNr = localBank.createAccount(((CreateAccountCommand)inputCmd).getValue());
					outputCmd = new CreatedAccountCommand(accountNr);
					break;
				case CloseAccountCommand.TYPE:
					boolean success = localBank.closeAccount(((CloseAccountCommand)inputCmd).getValue());
					outputCmd = new ClosedAccountCommand(success);
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
					try{
						Account localAccount = localBank.getAccount(dCmd.getAccountNr());
						localAccount.deposit(dCmd.getValue());
						outputCmd = new StatusCommand();
					} catch(InactiveException ie){
						outputCmd = new StatusCommand(StatusId.InactiveException);
					} catch(IllegalArgumentException iae){
						outputCmd = new StatusCommand(StatusId.IllegalArgumentException);
					}
					break;
				case WithdrawCommand.TYPE:
					WithdrawCommand wCmd = (WithdrawCommand)inputCmd;
					try{
						Account localAccount = localBank.getAccount(wCmd.getAccountNr());
						localAccount.withdraw(wCmd.getValue());
						outputCmd = new StatusCommand();
					} catch(InactiveException ie){
						outputCmd = new StatusCommand(StatusId.InactiveException);
					} catch(IllegalArgumentException iae){
						outputCmd = new StatusCommand(StatusId.IllegalArgumentException);
					} catch(OverdrawException oe){
						outputCmd = new StatusCommand(StatusId.OverdrawException);
					}
					break;
				default:
					outputCmd = new StatusCommand(StatusId.IllegalArgumentException);
					break;
			}
			outputCmd.send(outputStream);
			outputStream.flush();
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
