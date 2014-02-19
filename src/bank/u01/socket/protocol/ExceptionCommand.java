package bank.u01.socket.protocol;

public class ExceptionCommand extends StringCommand {
	public static enum ExceptionId { IllegalArgumentException, OverdrawException, InactiveException }
	public static final String TYPE = "exception";
	@Override public String getType() { return TYPE; }
	public ExceptionCommand() { super(); }
	public ExceptionCommand(ExceptionId value) { super(value.name()); }
}
