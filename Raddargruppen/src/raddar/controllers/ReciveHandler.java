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
			// Skapa en ServerSocket f�r att lyssna p� inkommande meddelanden
			ServerSocket so = new ServerSocket(port);

			while (true) 
				// N�r ett inkommande meddelande tas emot skapa en ny Receiver
				// som k�rs i en egen tr�d
				new Reciver(so.accept(),rc);

		} catch (IOException ie) {
			ie.printStackTrace();
		}
		
	}
}
