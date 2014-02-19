package bank.local;

import bank.InactiveException;
import bank.OverdrawException;

public class LocalAccount implements bank.Account {
	private String number;
	private String owner;
	private double balance = 0.0;
	private boolean active = true;
	
	//When we ignore Scalability:
	//TODO: Generated Number instead of counting up
	private static int nextNumber = 1;

	LocalAccount(String owner) {
		this.owner = owner;
		this.number = Integer.toString(LocalAccount.nextNumber++);
	}

	@Override
	public double getBalance() {
		return balance;
	}

	@Override
	public String getOwner() {
		return owner;
	}

	@Override
	public String getNumber() {
		return number;
	}

	@Override
	public boolean isActive() {
		return active;
	}
	
	/**
	 * Closes the LocalAccount
	 * Sets active to false
	 */
	protected boolean close(){
		if(balance != 0.0){ return false; }
		if(!active){ return false; }
		active = false;
		return true;
	}

	@Override
	public void deposit(double amount) throws InactiveException {
		if(!active){ throw new InactiveException(); }
		if(amount < 0){ throw new IllegalArgumentException(); }
		balance += amount;
	}

	@Override
	public void withdraw(double amount) throws InactiveException, OverdrawException {
		if(!active){ throw new InactiveException(); }
		if(amount < 0){ throw new IllegalArgumentException(); }
		if(amount > balance){ throw new OverdrawException(); }
		balance -= amount;
	}

}