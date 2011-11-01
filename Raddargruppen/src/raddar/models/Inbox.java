package raddar.models;

import java.util.ArrayList;

import android.util.Log;

import raddar.enums.MessageType;

public class Inbox{
	
	private ArrayList<Message> inbox;

	public Inbox(){
		inbox = new ArrayList<Message>();
		String lol = "mikaela";
		TextMessage t1 = new TextMessage(MessageType.TEXT, lol, lol);
		if(inbox.add(t1))
			Log.d("FAN", "lol");
		TextMessage t2 = new TextMessage(MessageType.TEXT, "Börje", "Börje");
		inbox.add(t2);
	}
	
	public void newMessage(Message m){
		inbox.add(m);
	}
	
	public void removeMessage(Message m){
		inbox.remove(m);
	}
	
	public Message getMessage(int n){
		return inbox.get(n);
	}
	public Object[] getMessage(){
		return  inbox.toArray();
	}
	public ArrayList<Message> getInbox(){
		return inbox;
	}
}
