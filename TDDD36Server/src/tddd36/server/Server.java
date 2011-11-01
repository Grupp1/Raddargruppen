package tddd36.server;

import java.io.IOException;
import java.net.ServerSocket;

public class Server {
	
	// Default-value: 6789
	private int port;
	
	/* 
	 * Alla anslutna enheter sparas i detta objekt associerade med sina IP-addresser
	 * Beh�ver servern veta vilken IP-address en viss anv�ndare har s� �r det fr�n detta
	 * objekt IP-addressen kan h�mtas 
	 */
	public static Associations onlineUsers;

	public Server() {
		this(6789);
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
				// Acceptera en inkommande klient och skapa en ny ClientHandler 
				// som hanterar klienten i en egen tr�d
				new ClientHandler(so.accept());
			
		} catch (IOException ie) {
			ie.printStackTrace();
		}
		
	}
	
	
	
	public static void main(String[] args) {
		new Server();
	}	
}
