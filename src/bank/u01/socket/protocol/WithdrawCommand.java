package bank.u01.socket.protocol;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class WithdrawCommand extends GenericCommand<Double> {
	private String accountNr;
	public static final String TYPE = "withdraw";
	@Override public String getType() { return TYPE; }
	public WithdrawCommand() {
		super(0.0);
		this.accountNr = "";
	}
	public WithdrawCommand(String account, Double value) {
		super(value);
		this.accountNr = account;
	}
	public String getAccountNr() {
		return accountNr;
	}
	public void setAccountNr(String account) {
		this.accountNr = account;
	}
	@Override
	protected void write(DataOutput stream) throws IOException {
		stream.writeUTF(accountNr);
		stream.writeDouble(getValue());
	}
	@Override
	public void read(DataInput stream) throws IOException {
		accountNr = stream.readUTF();
		setValue(stream.readDouble());
	}
}
