package bank.u01.socket.protocol;

import bank.u01.socket.protocol.SocketCommand.SocketCommandFactory;

public final class SocketUtil {

	private SocketUtil(){} //No instance possible

	/**
	 * Register recommended commands
	 */
	public static void registerCommands(){
		SocketCommand.addCommandFactory(new SocketCommandFactory() {
			@Override public String getType() { return EchoCommand.TYPE; }
			@Override public SocketCommand createCommand() { return new EchoCommand(); }
		});
	}
}
