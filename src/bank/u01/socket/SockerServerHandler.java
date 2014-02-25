package bank.u01.socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Set;

import bank.Account;
import bank.AccountBase;
import bank.Bank;
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
import bank.u01.socket.protocol.StatusCommand;
import bank.u01.socket.protocol.StatusCommand.StatusId;
import bank.u01.socket.protocol.GetAccountCommand;
import bank.u01.socket.protocol.GetAccountNumbersCommand;
import bank.u01.socket.protocol.SocketCommand;
import bank.u01.socket.protocol.TransferCommand;
import bank.u01.socket.protocol.WithdrawCommand;

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
			System.out.println(type);
			switch (type) {
				case EchoCommand.TYPE:
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
					AccountBase account = (AccountBase)localBank.getAccount(((GetAccountCommand)inputCmd).getValue());
					outputCmd = new AccountCommand(account);
					break;
				case TransferCommand.TYPE:
					TransferCommand tCmd = (TransferCommand)inputCmd;
					try {
						Account to = localBank.getAccount(tCmd.getTo().getNumber());
						Account from = localBank.getAccount(tCmd.getFrom().getNumber());
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
