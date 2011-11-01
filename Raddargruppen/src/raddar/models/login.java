package raddar.models;

import java.util.HashMap;
import java.util.Observable;

import raddar.enums.LoginResponse;

public class Login extends Observable {
	
	HashMap<String, String> passwordCache = new HashMap<String, String>();
	
	public Login(){
		passwordCache.put("admin", "password");
	}
	
	public LoginResponse checkPassword(String user, String hashedPassword){
		if (passwordCache.containsKey(user)){
			return checkLocal(user, hashedPassword);
		}
		else{
			return checkServer(user, hashedPassword);
		}
	}
	
	private LoginResponse checkLocal(String user, String password){
		String temp = passwordCache.get(user);
		if (temp == null){
			return LoginResponse.NO_SUCH_USER;
		}
		if (password.equals(temp)){
			return LoginResponse.ACCEPTED;
		}
		return LoginResponse.ERROR;
	}
	
	private LoginResponse checkServer(String user, String password){
		// send user and password to server via Sender!
		return LoginResponse.NO_CONNECTION;
	}
	
}
