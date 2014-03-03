package bank.u01.socket.protocol;

import java.util.HashSet;
import java.util.Set;

/**
 * Answer to GetAccountNumbersCommand
 * @see GetAccountNumbersCommand
 * @author JP
 */
public class AccountNumbersCommand extends StringSetCommand {
	public static final String TYPE = "anumbers";
	@Override public String getType() { return TYPE; }
	public AccountNumbersCommand() { super(new HashSet<String>()); }
	public AccountNumbersCommand(Set<String> value) { super(value); }
}
