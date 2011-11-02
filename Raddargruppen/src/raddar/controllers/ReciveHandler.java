package raddar.controllers;

import java.io.IOException;
import java.net.ServerSocket;

public class ReciveHandler implements Runnable {
	// Standard port = 6789
	private int port = 6789;
	private Thread reciveHandler = new Thread(this);
	private ReciveController rc;

	public ReciveHandler(ReciveController rc) {
		this.rc = rc;
		reciveHandler.start();
	}

	public ReciveHandler(ReciveController rc, int port) {
		this.port = port;
		this.rc = rc;
		reciveHandler.start();
	}


	public void run() {
		try {
			// Skapa en ServerSocket för att lyssna på inkommande meddelanden
			ServerSocket so = new ServerSocket(port);

			while (true) 
				// När ett inkommande meddelande tas emot skapa en ny Receiver
				// som körs i en egen tråd
				new Reciver(so.accept(),rc);

		} catch (IOException ie) {
			ie.printStackTrace();
		}
		
	}
}
