package bank.u07.websocket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

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

@ClientEndpoint
public class WebsocketBank implements Bank {

	//Thread synchronisation
	private Lock messageLock = new ReentrantLock();
	private Condition responded;
	private Condition gotData;
	private byte[] data = null;

	private Session session = null;
	/**
	 * Cached accounts. This hastable assures, that there exists only one instance of every account per account.nr and server
	 */
	private final Map<String, AccountBase> accounts = new HashMap<>();

	public WebsocketBank() {
		responded = messageLock.newCondition();
		gotData = messageLock.newCondition();
	}

	@OnOpen
	public void onOpen(Session session) throws IOException {
		this.session = session;
	}

	@OnClose
	public void onClose(Session session, CloseReason closeReason) {
		this.session = null;
	}

	/**
	 * Update notifications
	 */
	@OnMessage
	public void onMessage(Session session, String message) throws IOException {
		
	}

	/**
	 * Message answers
	 */
	@OnMessage
	public void onMessage(Session session, ByteBuffer message) throws IOException {
		messageLock.lock();
		data = message.array();
		responded.signal();
		try { gotData.await(); }
		catch (InterruptedException e) { throw new IOException(e); }
		finally { messageLock.unlock(); }
	}

	@SuppressWarnings("unchecked")
	protected <T extends SocketCommand> T sendCommand(SocketCommand outputCmd) throws IOException{
		messageLock.lock();
		byte[] msg = null;
		try {
		//Send request
		session.getBasicRemote().sendBinary(ByteBuffer.wrap(outputCmd.toBytes()));
		try { responded.await(); }
		catch (InterruptedException e) { throw new IOException(e); }
		//Get response
		msg = data;
		gotData.signal();
		} finally { messageLock.unlock(); }
		return (T) SocketCommand.fromBytes(msg);
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

	public class WebsocketAccount extends AccountBase {

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
