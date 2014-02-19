package bank;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;


public abstract class AccountBase implements Account {
	public abstract void setNumber(String number);
	public abstract void setOwner(String owner);
	public abstract void setBalance(double balance);
	public abstract void setActive(boolean active);	
	public void write(DataOutput stream) throws IOException {
		stream.writeUTF(getNumber());
		stream.writeUTF(getOwner());
		stream.writeDouble(getBalance());
		stream.writeBoolean(isActive());
	}
	public void read(DataInput stream) throws IOException {
		setNumber(stream.readUTF());
		setOwner(stream.readUTF());
		setBalance(stream.readDouble());
		setActive(stream.readBoolean());
	}
}
