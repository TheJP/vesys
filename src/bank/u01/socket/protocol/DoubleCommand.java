package bank.u01.socket.protocol;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Abstract Command which allows to write single double value commands
 * @author JP
 */
public abstract class DoubleCommand extends GenericCommand<Double> {
	public DoubleCommand(){
		super(0.0);
	}
	public DoubleCommand(Double value){
		super(value);
	}

	@Override
	protected void write(DataOutput stream) throws IOException {
		stream.writeDouble(getValue());
	}

	@Override
	public void read(DataInput stream) throws IOException {
		setValue(stream.readDouble());
	}
}
