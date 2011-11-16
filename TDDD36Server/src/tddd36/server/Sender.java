package tddd36.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import raddar.models.Message;

import com.google.gson.Gson;
/**
 * 
 * @author andbo265
 *
 */
public class Sender implements Runnable {
	
	private Thread thread = new Thread(this);
	
	private Message m;				// Meddelandet som ska skickas
	private InetAddress adr;		// Mottagarens IP-adress
	private int port;				// Porten som mottagaren lyssnar på
	
	public Sender(Message m, InetAddress address, int port) {
		// Spara undan argumenten i klassens instansvariabler
		this.m = m;				
		this.adr = address;
		this.port = port;
		thread.start();	// Starta denna tråden
	}
	
	public Sender(Message m, String toUser, int port) {
		// Hämta mottagarens IP-adress och låt annan konstruktor göra resten av jobbet
		this(m, Server.onlineUsers.getUserAddress(toUser), port);
	}
	
	public Sender(Message m, String toUser) {
		// Sätt port till 6789 och låt annan konstruktor göra resten av jobbet
		this(m, toUser, 4043);
	}
	
	public Sender(Message m, InetAddress address) {
		// Sätt port till 6789 och låt annan konstruktor göra resten av jobbet
		this(m, address, 6789);
	}

	@Override
	public void run() {
		try {
			// Kolla om vi har en address att skicka till innan vi skapar en anslutning
			if (adr == null) {
				System.out.println("Mottagarens IP-adress är inte känd. ");	// Skriv ut att vi inte känner till mottagarens adress
				return;
			}
			// Skapa en socket för att kunna skicka meddelandet till mottagaren
			Socket rSocket = new Socket(adr, port);
			
			Gson gson = new Gson();
			String send = m.getClass().getName()+"\r\n";
			send +=	gson.toJson(m);
			PrintWriter out = new PrintWriter(rSocket.getOutputStream(), true);
			out.println(send);
			
			// Skriv ut vilken sorts meddelande som har skickats
			System.out.println("["+rSocket.getInetAddress().getHostAddress()+"] << " + m.getType().toString() + " har vidarebefordats till " + m.getDestUser());
			
			out.close();
			rSocket.close();
			
		} catch (IOException e) {
			// Logga ut denna användaren om 
			LoginManager.logoutUser(m.getDestUser());
			// Mottagaren är inte online
			// Buffra meddelandet
			e.printStackTrace();
		}
	}
}
