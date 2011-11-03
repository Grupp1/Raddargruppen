package raddar.controllers;

import java.util.ArrayList;
import java.util.Observer;

import android.util.Log;

import raddar.models.Inbox;
import raddar.models.Message;

public class ReciveController {
	
	private Inbox inbox;

	public ReciveController(){
		inbox = new Inbox();
	}

	public void addObservertoInbox(Observer o) {		
		inbox.addObserver(o);
	}
	public void addToInbox(Message m){
		inbox.newMessage(m);
	}

	public ArrayList<Message> getInbox() {	
		return inbox.getInbox();
	}
}
