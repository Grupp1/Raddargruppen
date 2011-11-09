package tddd36.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;

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
				// Acceptera en inkommande klient och skapa en ny ClientHandler 
				// som hanterar klienten i en egen tr�d
				new Receiver(so.accept());
			
		} catch (IOException ie) {
			ie.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		
		//System.out.println(Database.evalutateUser("Kjell", "hamburgare"));
		
		System.out.println(Database.evalutateUser("Alice", "Longshot"));
		
		//Database.addUser("einar", "lakan", 'u', "staben");
		
		System.out.println(Database.getUserID("einar"));
				
				
		System.out.println(Database.getUserLevel("Alice"));
		/*System.out.println(Database.getUserLevel("ANDREAS"));
		System.out.println(Database.getUserGroup("Kjell"));
		System.out.println(Database.getUserGroup("Alise"));
		
		
		System.out.println(Database.getUserID("Alice"));
		System.out.println(Database.getUserID("Alise"));
		System.out.println(Database.getUsername(1));
		System.out.println(Database.getUsername(4));*/
		
/*		TextMessage meddelande;
		meddelande = new TextMessage(MessageType.TEXT, "testare", "alligator", "jag skriver n�got nu");
		meddelande.setSubject("Nytt m�te!");
		System.out.println("meddelandet �r: " + meddelande);
		Database.storeTextMessage(meddelande);*/
		
		
	/*	List<TextMessage> list = Database.getAllTextMessagesTo("Alice");
		
		for (TextMessage tm: list) {
			System.out.println(tm);
			System.out.println(" -------------------- ");
		}
	*/
		
		new Server();
	}	
}
