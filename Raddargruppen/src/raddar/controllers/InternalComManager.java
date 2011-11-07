package raddar.controllers;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import android.content.Context;

import raddar.models.Message;

/*
 * Sk�ter kommunikationen med anv�ndaren och det grafiska gr�nssnittet
 */
public class InternalComManager implements Observer {

	private ReciveController rc;
	private String user;
	
	public InternalComManager(Context con){
		this.rc = new ReciveController(con);
		new ReciveHandler(rc);
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

	public void addObserverToInbox(Observer o) {
		rc.addObservertoInbox(o);
	}

	public ArrayList<Message> getInbox() {
		return rc.getInbox();
	}

}
