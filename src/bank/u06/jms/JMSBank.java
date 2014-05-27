package bank.u06.jms;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.Queue;
import javax.jms.TemporaryQueue;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

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
import bank.u01.socket.protocol.GetAccountCommand;
import bank.u01.socket.protocol.GetAccountNumbersCommand;
import bank.u01.socket.protocol.SocketCommand;
import bank.u01.socket.protocol.StatusCommand;
import bank.u01.socket.protocol.StatusCommand.StatusId;
import bank.u01.socket.protocol.TransferCommand;
import bank.u01.socket.protocol.WithdrawCommand;

public class JMSBank implements Bank {

	private Context jndiContext;
	private ConnectionFactory factory;
	private Queue queue;
	/**
	 * Cached accounts. This hastable assures, that there exists only one instance of every account per account.nr and server
	 */
	private final Map<String, AccountBase> accounts = new HashMap<>();

	public JMSBank() throws NamingException {
		jndiContext = new InitialContext();
		factory = (ConnectionFactory) jndiContext.lookup("ConnectionFactory");
		queue = (Queue) jndiContext.lookup("BANK");
	}

	@SuppressWarnings("unchecked")
	protected <T extends SocketCommand> T sendCommand(SocketCommand outputCmd) throws IOException{
		try (JMSContext context = factory.createContext()) {
			//Create queue for input command
			TemporaryQueue tmpQueue = context.createTemporaryQueue();

			JMSProducer sender = context.createProducer().setJMSReplyTo(tmpQueue);
			JMSConsumer receiver = context.createConsumer(tmpQueue);

			sender.send(queue, outputCmd.toBytes());
			byte[] msg = receiver.receiveBody(byte[].class);
			return (T) SocketCommand.fromBytes(msg);	
		}
	}

	@Override
	public String createAccount(String owner) throws IOException {
		CreatedAccountCommand result = sendCommand(new CreateAccountCommand(owner));
		return result.getValue();
	}

	@Override
	public boolean closeAccount(String number) throws IOException {
		ClosedAccountCommand result = sendCommand(new CloseAccountCommand(number));
		return result.getValue();
	}

	@Override
	public Set<String> getAccountNumbers() throws IOException {
		AccountNumbersCommand result = sendCommand(new GetAccountNumbersCommand());
		return result.getValue();
	}

	@Override
	public Account getAccount(String number) throws IOException {
		AccountCommand result = sendCommand(new GetAccountCommand(number));
		//Every account is cached locally, so there exists only one instance per account.nr and per server
		if (accounts.containsKey(number) && result.getValue() != null) {
			AccountBase origin = accounts.get(number);
			origin.setActive(result.getValue().isActive());
			origin.setBalance(result.getValue().getBalance());
			origin.setOwner(result.getValue().getOwner());
		} else {
			accounts.put(number, result.getValue());
		}
		return accounts.get(number);
	}

	@Override
	public void transfer(Account from, Account to, double amount)
			throws IOException, IllegalArgumentException, OverdrawException,
			InactiveException {
		StatusCommand result = sendCommand(new TransferCommand(from.getNumber(), to.getNumber(), amount));
		//Throw exception if received one over the connection
		if (result.getValue().equals(StatusId.IllegalArgumentException.name())) {
			throw new IllegalArgumentException();
		} else if (result.getValue().equals(StatusId.OverdrawException.name())) {
			throw new OverdrawException();
		} else if (result.getValue().equals(StatusId.InactiveException.name())) {
			throw new InactiveException();
		}
		//Update given accounts from server
		//This bank is implemented to only distribute one instance per account.nr,
		//so updating the reference in the hashtable will update all other references
		getAccount(from.getNumber());
		getAccount(to.getNumber());
	}

	public class JMSAccount extends AccountBase {

		private String number;
		private String owner;
		private double balance = 0.0;
		private boolean active = true;

		@Override
		public String getNumber() throws IOException {
			return number;
		}

		@Override
		public String getOwner() throws IOException {
			return owner;
		}

		@Override
		public boolean isActive() throws IOException {
			return active;
		}

		@Override
		public double getBalance() throws IOException {
			return balance;
		}

		@Override
		public void setNumber(String number) {
			this.number = number;
		}

		@Override
		public void setOwner(String owner) {
			this.owner = owner;
		}

		@Override
		public void setBalance(double balance) {
			this.balance = balance;
		}

		@Override
		public void setActive(boolean active) {
			this.active = active;
		}

		@Override
		public void deposit(double amount) throws IOException,
				IllegalArgumentException, InactiveException {
			StatusCommand inputCmd = sendCommand(new DepositCommand(getNumber(), amount));
			if (inputCmd.getValue().equals(
					StatusId.IllegalArgumentException.name())) {
				throw new IllegalArgumentException();
			} else if (inputCmd.getValue().equals(
					StatusId.InactiveException.name())) {
				throw new InactiveException();
			}
			getAccount(getNumber());
		}

		@Override
		public void withdraw(double amount) throws IOException,
				IllegalArgumentException, OverdrawException, InactiveException {
			StatusCommand inputCmd = sendCommand(new WithdrawCommand(getNumber(), amount));
			if (inputCmd.getValue().equals(
					StatusId.IllegalArgumentException.name())) {
				throw new IllegalArgumentException();
			} else if (inputCmd.getValue().equals(
					StatusId.InactiveException.name())) {
				throw new InactiveException();
			}
			getAccount(getNumber());
		}
	}

}
