package raddar.models;

import java.util.HashMap;
import java.util.Observable;

import raddar.enums.loginResponse;

public class login extends Observable {
	
	HashMap<String, String> passwordCache = new HashMap<String, String>();
	
	public login(){
		passwordCache.put("admin", "password");
	}
	
	public loginResponse checkPassword(String user, String password){
		if (passwordCache.containsKey(user)){
			return checkLocal(user, password);
		}
		else{
			return checkServer(user, password);
		}
	}
	
	private loginResponse checkLocal(String user, String password){
		String temp = passwordCache.get(user);
		if (temp == null){
			return loginResponse.NO_SUCH_USER;
		}
		if (password.equals(temp)){
			return loginResponse.ACCEPTED;
		}
		return loginResponse.ERROR;
	}
	
	private loginResponse checkServer(String user, String password){
		// send user and password to server via Sender!
		return loginResponse.NO_CONNECTION;
	}
	
}
