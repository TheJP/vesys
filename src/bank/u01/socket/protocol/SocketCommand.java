package bank.u01.socket.protocol;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Command which can be sent trough the connection
 * @author JP
 *
 */
public abstract class SocketCommand {

	/**
	 * Factory to create an instance of the specific command type
	 * (Needed when only reading the type from a socket stream)
	 * @author JP
	 */
	public static abstract class SocketCommandFactory {
		/**
		 * Gets the Type of the command (transmitted over stream)
		 * @return
		 */
		public abstract String getType();
		/**
		 * Factory Method which creates the command
		 * @return
		 */
		public abstract SocketCommand createCommand();
	}

	/**
	 * Write the parameters to the stream
	 * @param stream
	 * @return
	 */
	protected abstract void write(DataOutput stream) throws IOException;
	
	/**
	 * Send the command and the parameters over the stream
	 * @param stream
	 * @throws IOException
	 */
	public void send(DataOutput stream) throws IOException {
		stream.writeUTF(getType());
		write(stream);
	}
	/**
	 * Read the command parameters from the stream
	 * @param stream
	 * @return
	 */
	public abstract void read(DataInput stream) throws IOException ;
	/**
	 * Gets the Type of the command (transmitted over stream)
	 * @return
	 */
	public abstract String getType();

	private static Map<String, SocketCommandFactory> avaibleCommands = new HashMap<>();
	public static void addCommandFactory(SocketCommandFactory sc){
		avaibleCommands.put(sc.getType(), sc);
	}

	/**
	 * Creates the next Command from the stream
	 * @param stream
	 * @return
	 * @throws IOException
	 */
	public static SocketCommand createCommand(DataInput stream) throws IOException{
		SocketCommand cmd = null;
		String cmdType = stream.readUTF();
		if(avaibleCommands.containsKey(cmdType)){
			cmd = avaibleCommands.get(cmdType).createCommand();
			cmd.read(stream);
		}
		return cmd;
	}
}
