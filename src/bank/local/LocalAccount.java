package bank.local;

import java.io.Serializable;

import bank.AccountBase;
import bank.InactiveException;
import bank.OverdrawException;

/**
 * Local account implemented for the local bank
 * @see LocalBank
 * @author JP
 *
 */
public class LocalAccount extends AccountBase implements Serializable {
	private static final long serialVersionUID = -4261168763008406729L;
	private String number;
	private String owner;
	private double balance = 0.0;
	private boolean active = true;
	
	//TODO: Generated Number instead of counting up to improve scalability
	private static int nextNumber = 1;

	public LocalAccount(String owner) {
		this.owner = owner;
		this.number = Integer.toString(LocalAccount.nextNumber++);
	}

	public LocalAccount() {
		this.owner = "";
		this.number = "";
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