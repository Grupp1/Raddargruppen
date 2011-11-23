package raddar.models;

import java.util.ArrayList;
import java.util.Observable;

import android.util.Log;

public class Inbox extends Observable {

	private ArrayList<Message> inbox;

	public Inbox(){
		inbox = new ArrayList<Message>();

	}
	
	public void newMessage(Message m){
		inbox.add(m);
		Log.d("inbox", ""+countObservers());
		setChanged();
		notifyObservers(inbox.get(inbox.size()-1).getSrcUser());
	}

	public void removeMessage(Message m){
		inbox.remove(m);
		notifyObservers();
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
