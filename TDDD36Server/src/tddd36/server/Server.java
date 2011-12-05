package tddd36.server;

import java.io.IOException;
import java.net.ServerSocket;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

public class Server {
	
	
	// Default-value: 6789
	private int port;
	
	/* 
	 * Alla anslutna enheter sparas i detta objekt associerade med sina IP-addresser
	 * Behöver servern veta vilken IP-address en viss användare har så är det från detta
	 * objekt IP-addressen kan hämtas 
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
			
			//Gamla icke ssl
			//ServerSocket so = new ServerSocket(port);
			
			//Printar info om server, nya ssl
			String userHome = System.getProperty( "user.home" );
            System.out.println(userHome);
			
            
            SSLServerSocketFactory sslserversocketfactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            SSLServerSocket sslserversocket = (SSLServerSocket) sslserversocketfactory.createServerSocket(port);
            
            System.out.println("Listening on port: " + port + "... ");

			while (true) 
				// Acceptera en inkommande klient och skapa en ny Receiver 
				// som hanterar klienten i en egen tråd
				
				//Gamla icke ssl
				//new Receiver(so.accept());
				
				//nya ssl			
				new Receiver((SSLSocket) sslserversocket.accept());
			
		} catch (IOException ie) {
			ie.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		//System.out.println(Database.getSalt("Alice"));
		
		new Server();
		
		//Database.addUser("magkj501", "magkj501", 'u', "users");
	}	
}
