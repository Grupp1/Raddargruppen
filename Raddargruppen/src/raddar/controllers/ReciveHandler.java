package raddar.controllers;

import java.io.IOException;
import java.net.ServerSocket;

import raddar.enums.MessageType;
import raddar.models.Message;
import raddar.views.MainView;
import android.content.Context;

public class ReciveHandler implements Runnable {
	// Standard port = 6789
	private int port = 4043;
	private Thread thread = new Thread(this);
	
	private Context context;

	public ReciveHandler(Context context) {
		this(4043, context);
	}

	public ReciveHandler( int port, Context context) {
		this.port = port;
		this.context = context;
		thread.start();
	}


	public void run() {
		try {
			// Skapa en ServerSocket f�r att lyssna p� inkommande meddelanden
			ServerSocket so = new ServerSocket(port);

			while (true) 
				// N�r ett inkommande meddelande tas emot skapa en ny Receiver
				// som k�rs i en egen tr�d
				new Receiver(so.accept(), this, context);

		} catch (IOException ie) {
			ie.printStackTrace();
		}
		
	}
	public void newMessage(MessageType mt, Message m){
		if(mt == MessageType.TEXT){
			MainView.db.addRow(m);
		}
	}
}
