package bank.u01.socket.protocol;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * 
 * @author JP
 */
public abstract class StringCommand extends SocketCommand {

	private String string;
	
	public StringCommand(){
		this.string = "";
	}
	public StringCommand(String string){
		this.string = string;
	}

	@Override
	protected void write(DataOutput stream) throws IOException {
		stream.writeUTF(string);
	}

	@Override
	public void read(DataInput stream) throws IOException {
		string = stream.readUTF();
	}

	public String getString() {
		return string;
	}
	public void setString(String string) {
		this.string = string;
	}
}
