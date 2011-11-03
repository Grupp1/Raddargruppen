package raddar.models;

import java.util.ArrayList;
import java.util.Observable;

import android.util.Log;

import raddar.enums.MessageType;

public class Inbox extends Observable {

	private ArrayList<Message> inbox;

	public Inbox(){
		inbox = new ArrayList<Message>();
		String lol = "mikaela";
		TextMessage t1 = new TextMessage(MessageType.TEXT, lol, lol);
		t1.setData("Hej, Detta är ett testmeddelande!");
		inbox.add(t1);
		TextMessage t2 = new TextMessage(MessageType.TEXT, "Börje", "Börje");
		t2.setData("Tjena, du kan inte fixa admin rättigheter? mvh Böare");
		inbox.add(t2);
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
