package bank.u01.socket.protocol;

import bank.Bank;
import bank.u01.socket.SocketBank;
import bank.u01.socket.protocol.SocketCommand.SocketCommandFactory;
import bank.u06.jms.JMSBank;

/**
 * Contains a method to create all needed socket factories
 * @author JP
 *
 */
public final class SocketUtil {

	private SocketUtil(){} //No instance possible

	/**
	 * Register recommended commands
	 */
	public static void registerCommands(){
		registerCommands(null);
	}

	/**
	 * Register recommended commands
	 * @param localBank Used to create SocketAccounts
	 */
	public static void registerCommands(final Bank localBank){
		SocketCommand.addCommandFactory(new SocketCommandFactory() {
			@Override public String getType() { return EchoCommand.TYPE; }
			@Override public SocketCommand createCommand() { return new EchoCommand(); }
		});
		SocketCommand.addCommandFactory(new SocketCommandFactory() {
			@Override public String getType() { return AccountCommand.TYPE; }
			@Override public SocketCommand createCommand() {
				if(localBank == null){
					return new AccountCommand();
				}else if(localBank instanceof SocketBank){
					return new AccountCommand(((SocketBank)localBank).new SocketAccount());
				}else if(localBank instanceof JMSBank){
					return new AccountCommand(((JMSBank)localBank).new JMSAccount());
				}
				return null;
			}
		});
		SocketCommand.addCommandFactory(new SocketCommandFactory() {
			@Override public String getType() { return AccountNumbersCommand.TYPE; }
			@Override public SocketCommand createCommand() { return new AccountNumbersCommand(); }
		});
		SocketCommand.addCommandFactory(new SocketCommandFactory() {
			@Override public String getType() { return CloseAccountCommand.TYPE; }
			@Override public SocketCommand createCommand() { return new CloseAccountCommand(); }
		});
		SocketCommand.addCommandFactory(new SocketCommandFactory() {
			@Override public String getType() { return ClosedAccountCommand.TYPE; }
			@Override public SocketCommand createCommand() { return new ClosedAccountCommand(); }
		});
		SocketCommand.addCommandFactory(new SocketCommandFactory() {
			@Override public String getType() { return CreateAccountCommand.TYPE; }
			@Override public SocketCommand createCommand() { return new CreateAccountCommand(); }
		});
		SocketCommand.addCommandFactory(new SocketCommandFactory() {
			@Override public String getType() { return CreatedAccountCommand.TYPE; }
			@Override public SocketCommand createCommand() { return new CreatedAccountCommand(); }
		});
		SocketCommand.addCommandFactory(new SocketCommandFactory() {
			@Override public String getType() { return DepositCommand.TYPE; }
			@Override public SocketCommand createCommand() { return new DepositCommand(); }
		});
		SocketCommand.addCommandFactory(new SocketCommandFactory() {
			@Override public String getType() { return StatusCommand.TYPE; }
			@Override public SocketCommand createCommand() { return new StatusCommand(); }
		});
		SocketCommand.addCommandFactory(new SocketCommandFactory() {
			@Override public String getType() { return GetAccountCommand.TYPE; }
			@Override public SocketCommand createCommand() { return new GetAccountCommand(); }
		});
		SocketCommand.addCommandFactory(new SocketCommandFactory() {
			@Override public String getType() { return GetAccountNumbersCommand.TYPE; }
			@Override public SocketCommand createCommand() { return new GetAccountNumbersCommand(); }
		});
		SocketCommand.addCommandFactory(new SocketCommandFactory() {
			@Override public String getType() { return TransferCommand.TYPE; }
			@Override public SocketCommand createCommand() { return new TransferCommand(); }
		});
		SocketCommand.addCommandFactory(new SocketCommandFactory() {
			@Override public String getType() { return WithdrawCommand.TYPE; }
			@Override public SocketCommand createCommand() { return new WithdrawCommand(); }
		});
	}
}
