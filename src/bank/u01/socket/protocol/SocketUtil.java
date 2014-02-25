package bank.u01.socket.protocol;

import bank.u01.socket.SocketBank;
import bank.u01.socket.protocol.SocketCommand.SocketCommandFactory;

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
	public static void registerCommands(final SocketBank localBank){
		SocketCommand.addCommandFactory(new SocketCommandFactory() {
			@Override public String getType() { return EchoCommand.TYPE; }
			@Override public SocketCommand createCommand() { return new EchoCommand(); }
		});
		SocketCommand.addCommandFactory(new SocketCommandFactory() {
			@Override public String getType() { return AccountCommand.TYPE; }
			@Override public SocketCommand createCommand() {
				if(localBank == null){
					return new AccountCommand();
				}else{
					return new AccountCommand(localBank.new SocketAccount());
				}
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
			@Override public SocketCommand createCommand() {
				if(localBank == null){
					return new TransferCommand();
				}else{
					return new TransferCommand(localBank.new SocketAccount(), localBank.new SocketAccount(), 0.0);
				}
			}
		});
		SocketCommand.addCommandFactory(new SocketCommandFactory() {
			@Override public String getType() { return WithdrawCommand.TYPE; }
			@Override public SocketCommand createCommand() { return new WithdrawCommand(); }
		});
	}
}
