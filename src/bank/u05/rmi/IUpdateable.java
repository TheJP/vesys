package bank.u05.rmi;

/**
 * Interface, which allows access to the update method (so clients can be notified)
 * @author JP
 */
public interface IUpdateable {
	/**
	 * Method, which does the update notifications
	 * @param accountNr
	 */
	void update(String number);
}
