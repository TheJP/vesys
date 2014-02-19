package bank.u01.socket.protocol;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import bank.AccountBase;
import bank.local.LocalAccount;

public class WithdrawCommand extends GenericCommand<Double> {
	private AccountBase account;
	public static final String TYPE = "withdraw";
	@Override public String getType() { return TYPE; }
	public WithdrawCommand() {
		super(0.0);
		this.account = new LocalAccount();
	}
	public WithdrawCommand(AccountBase account, Double value) {
		super(value);
		this.account = account;
	}
	public AccountBase getAccount() {
		return account;
	}
	public void setAccount(AccountBase account) {
		this.account = account;
	}
	@Override
	protected void write(DataOutput stream) throws IOException {
		account.write(stream);
		stream.writeDouble(getValue());
	}
	@Override
	public void read(DataInput stream) throws IOException {
		account.read(stream);
		setValue(stream.readDouble());
	}
}
