package bank.u01.socket.protocol;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class TransferCommand extends GenericCommand<Double>{
	private String from, to;
	public static final String TYPE = "transfer";
	@Override public String getType() { return TYPE; }

	public TransferCommand() {
		super(0.0);
		this.from = "";
		this.to = "";
	}
	public TransferCommand(String from, String to, double value) {
		super(value);
		this.from = from;
		this.to = to;
	}
	@Override protected void write(DataOutput stream) throws IOException {
		stream.writeUTF(from);
		stream.writeUTF(to);
		stream.writeDouble(getValue());
	}
	@Override public void read(DataInput stream) throws IOException {
		from = stream.readUTF();
		to = stream.readUTF();
		setValue(stream.readDouble());
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
}
