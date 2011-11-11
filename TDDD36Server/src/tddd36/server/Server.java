package tddd36.server;

import java.io.IOException;
import java.net.ServerSocket;

public class Server implements Runnable {

	private Thread thread = new Thread(this);
	// Default-value: 6789
	private int port;

	private boolean run = false;
	
	private ServerGUI serverGUI;

	/*
	 * Alla anslutna enheter sparas i detta objekt associerade med sina
	 * IP-addresser Beh�ver servern veta vilken IP-address en viss anv�ndare har
	 * s� �r det fr�n detta objekt IP-addressen kan h�mtas
	 */
	public static Associations onlineUsers = new Associations();

	public Server(ServerGUI gui) {
		this(4043, gui);
	}

	public Server(int port, ServerGUI gui) {
		this.serverGUI = gui;
		this.port = port;
		gui.setServer(this);
		thread.start();
	}

	@Override
	public void run() {
		try {
			ServerSocket so = new ServerSocket(port);

			while (true) {
				while (run)
					// Acceptera en inkommande klient och skapa en ny Receiver
					// som hanterar klienten i en egen tr�d
					new Receiver(so.accept(), serverGUI);
			}
		} catch (IOException ie) {
			ie.printStackTrace();
		}
	}

	public void setRun(boolean b) {
		run = b;
		if (run) 
			serverGUI.printOK("Listening on port " + port + "... ");
		else 
			serverGUI.print("Server stopped. ");
		
	}
		
	public boolean getRun() {
		return run;
	}
}
