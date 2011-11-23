package raddar.controllers;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Observable;

import android.util.Log;

import raddar.enums.ConnectionStatus;
import raddar.enums.MessageType;
import raddar.enums.ServerInfo;
import raddar.models.MapObject;
import raddar.models.Message;
import raddar.views.MainView;
import android.content.Context;

public class ReciveHandler extends Observable implements Runnable {
	private Thread thread = new Thread(this);
	
	private Context context;

	public ReciveHandler(Context context) {
		this.context = context;
		thread.start();
	}

	/**
	 * Every time new information comes in to the client a
	 * new thread is started to handle the message.
	 */
	public void run() {
		try {
			// Skapa en ServerSocket f�r att lyssna p� inkommande meddelanden
			ServerSocket so = new ServerSocket(ServerInfo.SERVER_PORT);

			while (true){
				// N�r ett inkommande meddelande tas emot skapa en ny Receiver
				// som k�rs i en egen tr�d
				new Receiver(so.accept(), this, context);
				notifyObservers(ConnectionStatus.CONNECTED);
			}
		} catch (IOException ie) {
			notifyObservers(ConnectionStatus.DISCONNECTED);
			Log.d("ReciveHandler", "Kunde inte ta emot meddelande, disconnected");
			//ie.printStackTrace();
		}
		
	}
	public void newMessage(MessageType mt, Message m,boolean notify){
		if(mt == MessageType.TEXT){
			DatabaseController.db.addRow(m,notify);
		}
	}
	public void newMapObject(MapObject o){
		MainView.mapCont.add(o);
	}
}
