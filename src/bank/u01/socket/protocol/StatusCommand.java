package bank.u01.socket.protocol;

/**
 * Answer to DepositCommand, WithdrawCommand and TransferCommand
 * @see DepositCommand
 * @see WithdrawCommand
 * @see TransferCommand
 * @author JP
 */
public class StatusCommand extends StringCommand {
	public static enum StatusId { Success, IllegalArgumentException, OverdrawException, InactiveException }
	public static final String TYPE = "exception";
	@Override public String getType() { return TYPE; }
	public StatusCommand() { this(StatusId.Success); }
	public StatusCommand(StatusId value) { super(value.name()); }
}
