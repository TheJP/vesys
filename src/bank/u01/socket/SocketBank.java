package bank.u01.socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
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
import bank.u01.socket.protocol.GetAccountCommand;
import bank.u01.socket.protocol.GetAccountNumbersCommand;
import bank.u01.socket.protocol.SocketCommand;
import bank.u01.socket.protocol.SocketUtil;
import bank.u01.socket.protocol.StatusCommand;
import bank.u01.socket.protocol.StatusCommand.StatusId;
import bank.u01.socket.protocol.TransferCommand;
import bank.u01.socket.protocol.WithdrawCommand;

public class SocketBank implements Bank {

	private String address;
	private int port;
	private final Map<String, AccountBase> accounts = new HashMap<>();

	public SocketBank(String address, int port) {
		SocketUtil.registerCommands(this);
		this.address = address;
		this.port = port;
	}

	/**
	 * Sends the command over the Socket connection
	 * 
	 * @param outputCmd
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	protected <T extends SocketCommand> T sendCommand(SocketCommand outputCmd)
			throws IOException {
		SocketCommand inputCmd = null;
		Socket clientSocket = null;
		try {
			clientSocket = new Socket(address, port);
			DataOutputStream outputStream = new DataOutputStream(
					clientSocket.getOutputStream());
			DataInputStream inputStream = new DataInputStream(
					clientSocket.getInputStream());
			outputCmd.send(outputStream);
			inputCmd = SocketCommand.createCommand(inputStream);
		} catch (Exception e) {
			if (e instanceof IOException) {
				throw e;
			}
			e.printStackTrace();
		} finally {
			if (clientSocket != null) {
				clientSocket.close();
			}
		}
		if (inputCmd == null) {
			throw new IOException("Unkown result");
		}
		try {
			return (T) inputCmd;
		} catch (Exception e) {
			throw new IOException("Unkown result");
		}
	}

	@Override
	public String createAccount(String owner) throws IOException {
		CreateAccountCommand outputCmd = new CreateAccountCommand(owner);
		CreatedAccountCommand inputCmd = sendCommand(outputCmd);
		return inputCmd.getValue();
	}

	@Override
	public boolean closeAccount(String number) throws IOException {
		CloseAccountCommand outputCmd = new CloseAccountCommand(number);
		ClosedAccountCommand inputCmd = sendCommand(outputCmd);
		return inputCmd.getValue();
	}

	@Override
	public Set<String> getAccountNumbers() throws IOException {
		GetAccountNumbersCommand outputCmd = new GetAccountNumbersCommand();
		AccountNumbersCommand inputCmd = sendCommand(outputCmd);
		return inputCmd.getValue();
	}

	@Override
	public Account getAccount(String number) throws IOException {
		GetAccountCommand outputCmd = new GetAccountCommand(number);
		AccountCommand inputCmd = sendCommand(outputCmd);
		if (accounts.containsKey(number) && inputCmd.getValue() != null) {
			AccountBase origin = accounts.get(number);
			origin.setActive(inputCmd.getValue().isActive());
			origin.setBalance(inputCmd.getValue().getBalance());
			origin.setOwner(inputCmd.getValue().getOwner());
		} else {
			accounts.put(number, inputCmd.getValue());
		}
		return accounts.get(number);
	}

	@Override
	public void transfer(Account from, Account to, double amount)
			throws IOException, IllegalArgumentException, OverdrawException,
			InactiveException {
		TransferCommand outputCmd = new TransferCommand(from.getNumber(), to.getNumber(), amount);
		StatusCommand inputCmd = sendCommand(outputCmd);
		if (inputCmd.getValue()
				.equals(StatusId.IllegalArgumentException.name())) {
			throw new IllegalArgumentException();
		} else if (inputCmd.getValue()
				.equals(StatusId.OverdrawException.name())) {
			throw new OverdrawException();
		} else if (inputCmd.getValue()
				.equals(StatusId.InactiveException.name())) {
			throw new InactiveException();
		}
		//TODO
		((AccountBase) from).setBalance(
				getAccount(from.getNumber()).getBalance());
		((AccountBase) to).setBalance(
				getAccount(to.getNumber()).getBalance());
	}

	public class SocketAccount extends AccountBase {

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
			DepositCommand outputCmd = new DepositCommand(getNumber(), amount);
			StatusCommand inputCmd = sendCommand(outputCmd);
			if (inputCmd.getValue().equals(
					StatusId.IllegalArgumentException.name())) {
				throw new IllegalArgumentException();
			} else if (inputCmd.getValue().equals(
					StatusId.InactiveException.name())) {
				throw new InactiveException();
			}
			setBalance(getAccount(this.getNumber()).getBalance());
		}

		@Override
		public void withdraw(double amount) throws IOException,
				IllegalArgumentException, OverdrawException, InactiveException {
			WithdrawCommand outputCmd = new WithdrawCommand(getNumber(), amount);
			StatusCommand inputCmd = sendCommand(outputCmd);
			if (inputCmd.getValue().equals(
					StatusId.IllegalArgumentException.name())) {
				throw new IllegalArgumentException();
			} else if (inputCmd.getValue().equals(
					StatusId.InactiveException.name())) {
				throw new InactiveException();
			}
			setBalance(getAccount(this.getNumber()).getBalance());
		}
	}
}