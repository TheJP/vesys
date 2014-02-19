package bank.u01.socket.protocol;

public class ClosedAccountCommand extends BooleanCommand {
	public static final String TYPE = "closeda";
	public ClosedAccountCommand() { super(); }
	public ClosedAccountCommand(Boolean value) { super(value); }
	@Override public String getType() { return TYPE; }
}
