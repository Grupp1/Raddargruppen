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
	private int port;				// Porten som mottagaren lyssnar p�
	
	public Sender(Message m, InetAddress address, int port) {
		// Spara undan argumenten i klassens instansvariabler
		this.m = m;				
		this.adr = address;
		this.port = port;
		thread.start();	// Starta denna tr�den
	}
	
	public Sender(Message m, String toUser, int port) {
		// H�mta mottagarens IP-adress och l�t annan konstruktor g�ra resten av jobbet
		this(m, Server.onlineUsers.getUserAddress(toUser), port);
	}
	
	public Sender(Message m, String toUser) {
		// S�tt port till 6789 och l�t annan konstruktor g�ra resten av jobbet
		this(m, toUser, 4043);
	}
	
	public Sender(Message m, InetAddress address) {
		// S�tt port till 6789 och l�t annan konstruktor g�ra resten av jobbet
		this(m, address, 6789);
	}

	@Override
	public void run() {
		try {
			// Kolla om vi har en address att skicka till innan vi skapar en anslutning
			if (adr == null) {
				System.out.println("Mottagarens IP-adress �r inte k�nd. ");	// Skriv ut att vi inte k�nner till mottagarens adress
				return;
			}
			// Skapa en socket f�r att kunna skicka meddelandet till mottagaren
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
			// Logga ut denna anv�ndaren om 
			LoginManager.logoutUser(m.getDestUser());
			// Mottagaren �r inte online
			// Buffra meddelandet
			e.printStackTrace();
		}
	}
}
