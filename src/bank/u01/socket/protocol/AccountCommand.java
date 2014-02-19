package bank.u01.socket.protocol;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import bank.AccountBase;
import bank.local.LocalAccount;

public class AccountCommand extends GenericCommand<AccountBase> {
	public static final String TYPE = "a";
	@Override public String getType() { return TYPE; }
	public AccountCommand() { super((AccountBase)new LocalAccount()); }
	public AccountCommand(AccountBase value) { super(value); }
	@Override protected void write(DataOutput stream) throws IOException { getValue().write(stream); }
	@Override public void read(DataInput stream) throws IOException { getValue().read(stream); }
}