package bank.u01.socket.protocol;

public class GetAccountCommand extends StringCommand {
	public static final String TYPE = "geta";
	@Override public String getType() { return TYPE; }
	public GetAccountCommand() { super(); }
	public GetAccountCommand(String string) { super(string); }
}
