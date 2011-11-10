package tddd36.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

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
		this(m, toUser, 6789);
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
				System.out.println("Receiver's address is unknown. ");	// Skriv ut att vi inte känner till mottagarens adress
				return;
			}
			// Skapa en socket för att kunna skicka meddelandet till mottagaren
			Socket rSocket = new Socket(adr, port);
			
			// Skapa en PrintWriter för att förenkla skickandet av meddelandet via socketen
			PrintWriter pw = new PrintWriter(rSocket.getOutputStream(), true);
			
			// Formatera meddelandet enligt HTTP-standard och skicka iväg det
			pw.println(m.getFormattedMessage());
			
			// Skriv ut vilken sorts meddelande som har skickats
			System.out.println("["+rSocket.getInetAddress().getHostAddress()+"] << " + m.getType().toString() + " has been sent. ");
			
			// Stäng ner utströmmen och socket när vi är färdiga
			pw.close();
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
