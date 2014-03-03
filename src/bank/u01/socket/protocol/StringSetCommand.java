package bank.u01.socket.protocol;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Abstract Command which allows to write commands which transmit a set of strings
 * @author JP
 */
public abstract class StringSetCommand extends GenericSetCommand<String> {

	public StringSetCommand(){
		super(new HashSet<String>());
	}
	public StringSetCommand(Set<String> value) {
		super(value);
	}

	@Override
	protected void writeValue(DataOutput stream, String value) throws IOException {
		stream.writeUTF(value);
	}

	@Override
	protected String readValue(DataInput stream) throws IOException {
		return stream.readUTF();
	}
}
