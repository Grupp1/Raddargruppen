package raddar.controllers;

import java.util.Observable;
import java.util.Observer;



/*
 * Sköter kommunikationen med användaren och det grafiska gränssnittet
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
