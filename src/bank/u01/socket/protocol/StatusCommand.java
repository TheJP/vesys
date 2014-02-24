package bank.u01.socket.protocol;

public class StatusCommand extends StringCommand {
	public static enum StatusId { Success, IllegalArgumentException, OverdrawException, InactiveException }
	public static final String TYPE = "exception";
	@Override public String getType() { return TYPE; }
	public StatusCommand() { this(StatusId.Success); }
	public StatusCommand(StatusId value) { super(value.name()); }
}
