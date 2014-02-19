package bank.u01.socket.protocol;

public class ClosedAccount extends BooleanCommand {
	public static final String TYPE = "closeda";
	public ClosedAccount() { super(); }
	public ClosedAccount(Boolean value) { super(value); }
	@Override public String getType() { return TYPE; }
}
