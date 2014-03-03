package bank.u01.socket.protocol;


/**
 * Abstract Command which allows to write single generic value commands
 * @author JP
 */
public abstract class GenericCommand<T> extends SocketCommand {

	private T value;

	public GenericCommand(T value){
		this.value = value;
	}

	public T getValue() {
		return value;
	}
	public void setValue(T value) {
		this.value = value;
	}
}
