package bank.u01.socket.protocol;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import bank.AccountBase;
import bank.local.LocalAccount;

/**
 * @author Janis
 *
 */
public class DepositCommand extends GenericCommand<Double> {
	private AccountBase account;
	public static final String TYPE = "deposit";
	@Override public String getType() { return TYPE; }
	public DepositCommand() {
		super(0.0);
		this.account = new LocalAccount();
	}
	public DepositCommand(AccountBase account, Double value) {
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
