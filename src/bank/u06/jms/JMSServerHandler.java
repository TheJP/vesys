package bank.u06.jms;

import java.util.Set;

import javax.jms.Destination;
import javax.jms.JMSProducer;

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
import bank.u05.rmi.IUpdateable;


/**
 * Handles a single client and stops afterwards
 * @author JP
 *
 */
public class JMSServerHandler implements Runnable {

	private JMSProducer sender;
	private Destination destination;
	private SocketCommand inputCmd;
	private IUpdateable updater;
	/**
	 * Local bank implementation which client request are executed on.
	 * (May also be a remote bank implementation)
	 */
	private BankBase localBank;
	public JMSServerHandler(JMSProducer sender, Destination destination, IUpdateable updater, SocketCommand inputCmd, BankBase localBank){
		this.sender = sender;
		this.destination = destination;
		this.updater = updater;
		this.inputCmd = inputCmd;
		this.localBank = localBank;
	}

	@Override
	public void run() {
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
					updater.update(accountNr);
					break;
				case CloseAccountCommand.TYPE:
					boolean success = localBank.closeAccount(((CloseAccountCommand)inputCmd).getValue());
					outputCmd = new ClosedAccountCommand(success);
					updater.update(((CloseAccountCommand)inputCmd).getValue());
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
						updater.update(to.getNumber());
						updater.update(from.getNumber());
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
					if(localAccount != null){ updater.update(localAccount.getNumber()); }
					break;
				case WithdrawCommand.TYPE:
					WithdrawCommand wCmd = (WithdrawCommand)inputCmd;
					Account localAccount2 = null;
					try{
						localAccount2 = localBank.getAccount(wCmd.getAccountNr());
						localAccount2.withdraw(wCmd.getValue());
						outputCmd = new StatusCommand();
						updater.update(localAccount2.getNumber());
					} catch(InactiveException ie){
						outputCmd = new StatusCommand(StatusId.InactiveException);
					} catch(IllegalArgumentException iae){
						outputCmd = new StatusCommand(StatusId.IllegalArgumentException);
					} catch(OverdrawException oe){
						outputCmd = new StatusCommand(StatusId.OverdrawException);
					}
					if(localAccount2 != null){ updater.update(localAccount2.getNumber()); }
					break;
				default:
					outputCmd = new StatusCommand(StatusId.IllegalArgumentException);
					break;
			}
			sender.send(destination, outputCmd.toBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
