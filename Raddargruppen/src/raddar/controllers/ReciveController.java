package raddar.controllers;

import java.util.ArrayList;
import java.util.Observer;

import android.content.Context;
import android.util.Log;

import raddar.enums.MessageType;
import raddar.models.ClientDatabaseManager;
import raddar.models.Inbox;
import raddar.models.Message;
import raddar.models.TextMessage;

public class ReciveController {
	
	private ClientDatabaseManager inbox;

	public ReciveController(Context con){
		inbox = new ClientDatabaseManager(con, "Alice");
	}

	public void addObservertoInbox(Observer o) {		
		inbox.addObserver(o);
	}
	public void addToInbox(Message m){
		inbox.addRow(m);
	}

	public ArrayList<Message> getInbox() {	
		return inbox.getAllRowsAsArrays();
	}
}
