package bank.u01.socket.protocol;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public abstract class BooleanCommand extends GenericCommand<Boolean> {
	public BooleanCommand(){
		super(false);
	}
	public BooleanCommand(Boolean value) {
		super(value);
	}

	@Override
	protected void write(DataOutput stream) throws IOException {
		stream.writeBoolean(getValue());
	}

	@Override
	public void read(DataInput stream) throws IOException {
		setValue(stream.readBoolean());
	}
}
