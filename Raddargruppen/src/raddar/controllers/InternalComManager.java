package raddar.controllers;

import java.util.Observable;
import java.util.Observer;



/*
 * Sk�ter kommunikationen med anv�ndaren och det grafiska gr�nssnittet
 */
public class InternalComManager implements Observer {

	private String user;
	
	public InternalComManager(){
		
	}

	public String setUser(String user) {
		String temp = getUser();
		this.user = user;
		return temp;
	}
	
	public String getUser() {
		return user;
	}

	public void update(Observable observable, Object data) {

	}

}
