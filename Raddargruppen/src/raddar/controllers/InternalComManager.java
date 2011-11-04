package raddar.controllers;


import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import android.content.Context;

import raddar.models.Message;


/*
 * Sköter kommunikationen med användaren och det grafiska gränssnittet
 */
 public class InternalComManager implements Observer {

	private ReciveController rc;
	
	public InternalComManager(Context con){
		this.rc = new ReciveController(con);
		new ReciveHandler(rc);
	}
	public void update(Observable observable, Object data) {
		
		
	}
	public void addObserverToInbox(Observer o){
		rc.addObservertoInbox(o);
	}

	public ArrayList<Message> getInbox() {
		return rc.getInbox();
	}

}
