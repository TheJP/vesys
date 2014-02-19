package bank.u01.socket.protocol;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public abstract class GenericSetCommand<T> extends GenericCommand<Set<T>> {
	public GenericSetCommand(Set<T> value){
		super(value);
	}
	
	@Override
	protected void write(DataOutput stream) throws IOException {
		stream.writeInt(getValue().size());
		for(T value : getValue()){
			writeValue(stream, value);
		}
	}

	@Override
	public void read(DataInput stream) throws IOException {
		int size = stream.readInt();
		Set<T> value = new HashSet<>(size);
		for(int i = 0; i < size; i++){
			value.add(readValue(stream));
		}
		setValue(value);
	}

	/**
	 * Writes a single value from the set 
	 * @param stream 
	 * @param value
	 * @throws IOException
	 */
	protected abstract void writeValue(DataOutput stream, T value) throws IOException;
	/**
	 * Reads a single value from the set
	 * @param stream
	 * @return Value
	 * @throws IOException
	 */
	protected abstract T readValue(DataInput stream) throws IOException;
}
