package bank.u01.socket.protocol;

/**
 * Command to create a new account
 * @see CreatedAccountCommand
 * @author JP
 */
public class CreateAccountCommand extends StringCommand {

	public CreateAccountCommand() { super(); }
	public CreateAccountCommand(String string) { super(string); }

	@Override
	public String getType() { return "createa"; }
}