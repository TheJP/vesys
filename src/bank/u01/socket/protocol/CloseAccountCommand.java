package bank.u01.socket.protocol;

/**
 * Command to close an account
 * @see ClosedAccountCommand
 * @author JP
 */
public class CloseAccountCommand extends StringCommand {
	public static final String TYPE = "closea";
	@Override public String getType() { return TYPE; }
	public CloseAccountCommand() { super(); }
	public CloseAccountCommand(String string) { super(string); }
}
