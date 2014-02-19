package bank.u01.socket.protocol;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * 
 * @author JP
 */
public abstract class StringCommand extends GenericCommand<String> {
	public StringCommand(){
		super("");
	}
	public StringCommand(String value){
		super(value);
	}

	@Override
	protected void write(DataOutput stream) throws IOException {
		stream.writeUTF(getValue());
	}

	@Override
	public void read(DataInput stream) throws IOException {
		setValue(stream.readUTF());
	}
}
