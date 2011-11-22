package raddar.controllers;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Observable;

import android.util.Log;

import raddar.enums.ConnectionStatus;
import raddar.enums.MessageType;
import raddar.models.MapObject;
import raddar.models.Message;
import raddar.views.MainView;

public class ReciveHandler extends Observable implements Runnable {
	// Standard port = 6789
	private int port = 4043;
	private Thread reciveHandler = new Thread(this);

	public ReciveHandler(MainView m){
		addObserver(m);
		reciveHandler.start();
	}

	public ReciveHandler(MainView m, int port){
		addObserver(m);
		this.port = port;
		reciveHandler.start();
	}


	public void run() {
		try {
			// Skapa en ServerSocket f�r att lyssna p� inkommande meddelanden
			ServerSocket so = new ServerSocket(port);

			while (true){
				// N�r ett inkommande meddelande tas emot skapa en ny Receiver
				// som k�rs i en egen tr�d
				new Receiver(so.accept(),this);
				notifyObservers(ConnectionStatus.CONNECTED);
			}
		} catch (IOException ie) {
			notifyObservers(ConnectionStatus.DISCONNECTED);
			Log.d("ReciveHandler", "Kunde inte ta emot meddelande, disconnected");
			//ie.printStackTrace();
		}
		
	}
	public void newMessage(MessageType mt, Message m){
		if(mt == MessageType.TEXT){
			MainView.db.addRow(m);
		}
	}
	public void newMapObject(MapObject o){
		MainView.mapCont.add(o);
	}
}
