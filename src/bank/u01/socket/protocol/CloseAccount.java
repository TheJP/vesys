package bank.u01.socket.protocol;

public class CloseAccount extends StringCommand {
	public static final String TYPE = "closea";
	public CloseAccount() { super(); }
	public CloseAccount(String string) { super(string); }
	@Override public String getType() { return TYPE; }
}
