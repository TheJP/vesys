package bank.u01.socket.protocol;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import bank.AccountBase;
import bank.local.LocalAccount;

/**
 * Command which can transmit a nullable account over a socket connection
 * Answer to GetAccountCommand
 * @see GetAccountCommand
 * @author JP
 */
public class AccountCommand extends GenericCommand<AccountBase> {
	public static final String TYPE = "a";
	@Override public String getType() { return TYPE; }
	public AccountCommand() { super((AccountBase)new LocalAccount()); }
	public AccountCommand(AccountBase value) { super(value); }
	@Override protected void write(DataOutput stream) throws IOException {
		stream.writeBoolean(getValue() != null);
		if(getValue() != null){
			getValue().write(stream);
		}
	}
	@Override public void read(DataInput stream) throws IOException {
		boolean hasValue = stream.readBoolean();
		if(hasValue){
			getValue().read(stream);
		}else{
			setValue(null);
		}
	}
}
