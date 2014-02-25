package bank.u01.socket.protocol;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * @author Janis
 *
 */
public class DepositCommand extends GenericCommand<Double> {
	private String accountNr;
	public static final String TYPE = "deposit";
	@Override public String getType() { return TYPE; }
	public DepositCommand() {
		super(0.0);
		this.accountNr = "";
	}
	public DepositCommand(String accountNr, Double value) {
		super(value);
		this.accountNr = accountNr;
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
