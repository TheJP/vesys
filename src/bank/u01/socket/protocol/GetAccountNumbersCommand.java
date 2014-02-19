package bank.u01.socket.protocol;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class GetAccountNumbersCommand extends SocketCommand {
	public static final String TYPE = "getas";
	@Override public String getType() { return TYPE; }
	@Override protected void write(DataOutput stream) throws IOException { }
	@Override public void read(DataInput stream) throws IOException { }
}
