package bank;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Abstract account, which requires getter and setter and implements read and write functionality for streams
 * @author JP
 *
 */
public abstract class AccountBase implements Account {
	public abstract void setNumber(String number);
	public abstract void setOwner(String owner);
	public abstract void setBalance(double balance);
	public abstract void setActive(boolean active);
	/**
	 * Method which writes the account to the stream
	 * Has to be overridden when more transmittable fields are added to account
	 * @param stream
	 * @throws IOException
	 */
	public void write(DataOutput stream) throws IOException {
		stream.writeUTF(getNumber());
		stream.writeUTF(getOwner());
		stream.writeDouble(getBalance());
		stream.writeBoolean(isActive());
	}
	/**
	 * Method which reads the account from the stream
	 * Has to be overridden when more transmittable fields are added to account
	 * @param stream
	 * @throws IOException
	 */
	public void read(DataInput stream) throws IOException {
		setNumber(stream.readUTF());
		setOwner(stream.readUTF());
		setBalance(stream.readDouble());
		setActive(stream.readBoolean());
	}
}
