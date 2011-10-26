package raddar.controllers;

import java.io.IOException;
import java.net.ServerSocket;

public class ReciveHandler implements Runnable {
	// Standard port = 6789
	private int port = 6789;
	private Thread reciveHandler = new Thread(this);

	public ReciveHandler() {
		reciveHandler.start();
	}

	public ReciveHandler(int port) {
		this.port = port;
		reciveHandler.start();
	}


	public void run() {
		try {
			ServerSocket so = new ServerSocket(port);

			while (true) 
				
				new Reciver(so.accept());

		} catch (IOException ie) {
			ie.printStackTrace();
		}
		
	}
}
