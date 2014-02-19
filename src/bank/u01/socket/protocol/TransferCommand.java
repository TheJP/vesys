package bank.u01.socket.protocol;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import bank.AccountBase;
import bank.local.LocalAccount;

public class TransferCommand extends GenericCommand<Double>{
	private AccountBase from, to;
	public static final String TYPE = "transfer";
	@Override public String getType() { return TYPE; }

	public TransferCommand() {
		super(0.0);
		this.from = new LocalAccount();
		this.to = new LocalAccount();
	}
	public TransferCommand(AccountBase from, AccountBase to, double value) {
		super(value);
		this.from = from;
		this.to = to;
	}
	@Override protected void write(DataOutput stream) throws IOException {
		from.write(stream);
		to.write(stream);
		stream.writeDouble(getValue());
	}
	@Override public void read(DataInput stream) throws IOException {
		from.read(stream);
		to.read(stream);
		setValue(stream.readDouble());
	}
	public AccountBase getFrom() {
		return from;
	}
	public void setFrom(AccountBase from) {
		this.from = from;
	}
	public AccountBase getTo() {
		return to;
	}
	public void setTo(AccountBase to) {
		this.to = to;
	}
}
