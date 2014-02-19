package bank.u01.socket.protocol;

/**
 * Command repplied if an account was created
 * @see CreateAccountCommand
 * @author JP
 */
public class CreatedAccountCommand extends StringCommand {
	public static final String TYPE = "createda";
	public CreatedAccountCommand() { super(); }
	public CreatedAccountCommand(String string) { super(string); }
	@Override public String getType() { return TYPE; }
}
