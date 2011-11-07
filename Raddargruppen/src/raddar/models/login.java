package raddar.models;

import java.util.HashMap;
import java.util.Observable;

import raddar.enums.LoginResponse;

public class Login extends Observable {
	
	private static HashMap<String, String> passwordCache = new HashMap<String, String>();
		
	public static LoginResponse checkPassword(String user, String password){
		if (passwordCache.containsKey(user)){
			return checkLocal(user, password);
		}
		else{
			return checkServer(user, password);
		}
	}
	
	private static LoginResponse checkLocal(String user, String password){
		String temp = passwordCache.get(user);
		if (temp == null){
			return LoginResponse.NO_SUCH_USER;
		}
		if (password.equals(temp)){
			return LoginResponse.ACCEPTED;
		}
		return LoginResponse.ERROR;
	}
	
	private static LoginResponse checkServer(String user, String password){
		// send user and password to server via Sender!
		return LoginResponse.NO_CONNECTION;
	}
	
	public static String cache(String username, String password) {
		return passwordCache.put(username, password);
	}
	
	public static String removeCache(String username) {
		return passwordCache.remove(username);
	}
	
}
