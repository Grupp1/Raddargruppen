package raddar.controllers;

import java.io.IOException;
import java.net.ServerSocket;

public class ReciveHandler implements Runnable{
	private int port;
	private Thread reciveHandler = new Thread(this);

	public ReciveHandler() {
		
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
