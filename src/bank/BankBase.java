package bank;

import java.io.IOException;

/**
 * Extends Bank with a single Method, which assures,
 * that the access to accounts returns instances of AccountBase
 * @author JP
 *
 */
public interface BankBase extends Bank {
	/**
	 * Returns a particular account given the account number. If the account
	 * number is not valid, <code>null</code> is returned as result. The returned
	 * account may be passive.
	 * 
	 * @param number number of the account
	 * @return account with the account number as specified or <code>null</code>,
	 *         if such an account was never created and does not exist.
	 * @throws IOException if a remoting or communication problem occurs
	 */
	AccountBase getAccountBase(String number) throws IOException;
}
