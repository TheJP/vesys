package bank.u02.http;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import bank.Account;
import bank.AccountBase;
import bank.Bank;
import bank.InactiveException;
import bank.OverdrawException;

/**
 * Bank implementation, which tries to read and write all data from and to a remote bank over http connections
 * @author JP
 *
 */
public class HttpBank implements Bank {

	/**
	 * Address for the http connections
	 */
	private String address;
	/**
	 * Port for the http connections
	 */
	private int port;
	/**
	 * Cached accounts. This hastable assures, that there exists only one instance of every HttpAccount per account.nr and HttpBank
	 */
	private final Map<String, AccountBase> accounts = new HashMap<>();

	public HttpBank(String address, int port) {
		this.address = address;
		this.port = port;
	}
	
	private JsonObject request(String method, String post) throws IOException, InactiveException, OverdrawException{
		URL url = new URL("http", address, port, "/api/bank/"+method);
		HttpURLConnection c = (HttpURLConnection) url.openConnection();
		c.setRequestMethod("POST");
		c.setUseCaches(false);
		c.setChunkedStreamingMode(256);
		c.setDoOutput(true);
		c.connect();
		//Send post if avaible
		if(post != null){
			c.getOutputStream().write(post.getBytes());
		}
		JsonReader reader = Json.createReader(c.getInputStream());
		JsonObject jsonObject = reader.readObject();
		//Throw given Exceptions
		if(jsonObject.containsKey("Error")){
			switch(jsonObject.getString("Error")){
				case "IllegalArgument": throw new IllegalArgumentException();
				case "Inactive": throw new InactiveException();
				case "Overdraw": throw new OverdrawException();
			}
		}
		return jsonObject;
	}

	@Override
	public String createAccount(String owner) throws IOException {
		JsonObject result = null;
		try {
			result = request("create", "Owner=" + URLEncoder.encode(owner, "UTF-8"));
		} catch (InactiveException | OverdrawException e) { }
		return result.getJsonNumber("Number").toString();
	}

	@Override
	public boolean closeAccount(String number) throws IOException {
		JsonObject result = null;
		try {
			result = request("close", "Number=" + URLEncoder.encode(number, "UTF-8"));
		} catch (InactiveException | OverdrawException e) { }
		return result.getBoolean("Success");
	}

	@Override
	public Set<String> getAccountNumbers() throws IOException {
		JsonObject result = null;
		try {
			result = request("getnrs", null);
		} catch (InactiveException | OverdrawException e) { }
		HashSet<String> numbers = new HashSet<>();
		for(Object s : result.getJsonArray("Numbers")){
			numbers.add(s.toString());
		}
		return numbers;
	}

	@Override
	public Account getAccount(String number) throws IOException {
		JsonObject result = null;
		try {
			result = request("get", "Number=" + URLEncoder.encode(number, "UTF-8"));
		} catch (InactiveException | OverdrawException e) { }
		//Every account is cached locally, so there exists only one instance per account.nr and per Instance
		if(!result.containsKey("Error")){
			if (accounts.containsKey(number)) {
				AccountBase origin = accounts.get(number);
				origin.setActive(result.getBoolean("Active"));
				origin.setBalance(result.getJsonNumber("Balance").doubleValue());
				origin.setOwner(result.getString("Owner"));
			} else {
				accounts.put(number,new HttpAccount(number,
					result.getString("Owner"),
					result.getJsonNumber("Balance").doubleValue(),
					result.getBoolean("Active")));
			}
			return accounts.get(number);
		}
		return null;
	}

	@Override
	public void transfer(Account from, Account to, double amount)
			throws IOException, IllegalArgumentException, OverdrawException,
			InactiveException {
		request("transfer", "From=" + URLEncoder.encode(from.getNumber(), "UTF-8") + 
			"&To=" + URLEncoder.encode(to.getNumber(), "UTF-8") +
			"&Amount=" + URLEncoder.encode(Double.toString(amount), "UTF-8"));
		//Update given accounts from server
		//This bank is implemented to only distribute one instance per account.nr,
		//so updating the reference in the hashtable will update all other references
		getAccount(from.getNumber());
		getAccount(to.getNumber());
	}

	/**
	 * Account implementation which is used by the http bank
	 * It's deposit and withdraw methods also work over http connections
	 * @author JP
	 *
	 */
	public class HttpAccount extends AccountBase {

		private String number;
		private String owner;
		private double balance = 0.0;
		private boolean active = true;
		
		public HttpAccount(String number, String owner, double balance,
				boolean active) {
			this.number = number;
			this.owner = owner;
			this.balance = balance;
			this.active = active;
		}

		@Override
		public String getNumber() throws IOException {
			return number;
		}

		@Override
		public String getOwner() throws IOException {
			return owner;
		}

		@Override
		public boolean isActive() throws IOException {
			return active;
		}

		@Override
		public double getBalance() throws IOException {
			return balance;
		}

		@Override
		public void setNumber(String number) {
			this.number = number;
		}

		@Override
		public void setOwner(String owner) {
			this.owner = owner;
		}

		@Override
		public void setBalance(double balance) {
			this.balance = balance;
		}

		@Override
		public void setActive(boolean active) {
			this.active = active;
		}

		@Override
		public void deposit(double amount) throws IOException,
				IllegalArgumentException, InactiveException {
			try {
				request("deposit", "Number=" + URLEncoder.encode(getNumber(), "UTF-8") +
						"&Amount=" + URLEncoder.encode(Double.toString(amount), "UTF-8"));
			} catch (OverdrawException e) { }
			//Update given accounts from server
			//This bank is implemented to only distribute one instance per account.nr,
			//so updating the reference in the hashtable will update all other references
			getAccount(getNumber());
		}

		@Override
		public void withdraw(double amount) throws IOException,
				IllegalArgumentException, OverdrawException, InactiveException {
			request("withdraw", "Number=" + URLEncoder.encode(getNumber(), "UTF-8") +
					"&Amount=" + URLEncoder.encode(Double.toString(amount), "UTF-8"));
			//Update given accounts from server
			//This bank is implemented to only distribute one instance per account.nr,
			//so updating the reference in the hashtable will update all other references
			getAccount(getNumber());
		}
	}
}