package tddd36.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

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
		this(m, toUser, 6789);
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
				System.out.println("Receiver's address is unknown. ");	// Skriv ut att vi inte k�nner till mottagarens adress
				return;
			}
			// Skapa en socket f�r att kunna skicka meddelandet till mottagaren
			Socket rSocket = new Socket(adr, port);
			
			// Skapa en PrintWriter f�r att f�renkla skickandet av meddelandet via socketen
			PrintWriter pw = new PrintWriter(rSocket.getOutputStream(), true);
			
			// Formatera meddelandet enligt HTTP-standard och skicka iv�g det
			pw.println(m.getFormattedMessage());
			
			// Skriv ut vilken sorts meddelande som har skickats
			System.out.println("["+rSocket.getInetAddress().getHostAddress()+"] << " + m.getType().toString() + " has been sent. ");
			
			// St�ng ner utstr�mmen och socket n�r vi �r f�rdiga
			pw.close();
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
