package raddar.controllers;

import java.io.IOException;
import java.net.ServerSocket;

import raddar.enums.MessageType;
import raddar.models.Message;
import raddar.views.MainView;

public class ReciveHandler implements Runnable {
	// Standard port = 6789
	private int port = 4043;
	private Thread reciveHandler = new Thread(this);

	/**
	 * Start a new ReciveHandler to listen on the standard port.
	 */
	public ReciveHandler() {
		reciveHandler.start();
	}
	/**
	 * Starts new ReciveHandler listening on a port
	 * @param port the port to listen to
	 */
	public ReciveHandler( int port) {
		this.port = port;
		reciveHandler.start();
	}

	/**
	 * Every time new information comes in to the client a
	 * new thread is started to handle the message.
	 */
	public void run() {
		try {
			// Skapa en ServerSocket för att lyssna på inkommande meddelanden
			ServerSocket so = new ServerSocket(port);

			while (true) 
				// När ett inkommande meddelande tas emot skapa en ny Receiver
				// som körs i en egen tråd
				new Receiver(so.accept(),this);

		} catch (IOException ie) {
			ie.printStackTrace();
		}
		
	}
	public void newMessage(MessageType mt, Message m){
		if(mt == MessageType.TEXT){
			DatabaseController.db.addRow(m);
		}
	}
}
