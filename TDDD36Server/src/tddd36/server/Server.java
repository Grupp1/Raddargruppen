package tddd36.server;

import java.io.IOException;
import java.net.ServerSocket;

import raddar.models.TextMessage;

public class Server {
	
	// Default-value: 6789
	private int port;
	
	/* 
	 * Alla anslutna enheter sparas i detta objekt associerade med sina IP-addresser
	 * Beh�ver servern veta vilken IP-address en viss anv�ndare har s� �r det fr�n detta
	 * objekt IP-addressen kan h�mtas 
	 */
	public static Associations onlineUsers = new Associations();

	public Server() {
		this(4043);
	}
	
	public Server(int port) {
		this.port = port;
		startServer();
	}
	
	private void startServer() {
		try {
			ServerSocket so = new ServerSocket(port);
			
			System.out.println("Listening on port: " + port + "... ");
			
			while (true) 
				// Acceptera en inkommande klient och skapa en ny Receiver 
				// som hanterar klienten i en egen tr�d
				new Receiver(so.accept());
			
		} catch (IOException ie) {
			ie.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		System.out.println(Database.getSalt("Alice"));
		new Server();
	}	
}
