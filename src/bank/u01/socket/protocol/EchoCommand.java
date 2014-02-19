package bank.u01.socket.protocol;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class EchoCommand extends SocketCommand {

	public static final String TYPE = "e";
	private String text;

	public EchoCommand(){
		this.text = "";
	}
	public EchoCommand(String text){
		this.text = text; 
	}

	@Override
	public void write(DataOutput stream) throws IOException {
		stream.writeUTF(this.text);
	}

	@Override
	public void read(DataInput stream) throws IOException {
		this.text = stream.readUTF();
	}

	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
}
