package tddd36.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

import raddar.models.MapObject;
import raddar.models.Message;
import raddar.models.RequestMessage;
import raddar.models.TextMessage;

import com.google.gson.Gson;
/**
 * 
 * @author andbo265
 *
 */
public class Sender implements Runnable {

	private Thread thread = new Thread(this);
	private InetAddress adr;		// Mottagarens IP-adress
	private int port;	// Porten som mottagaren lyssnar på
	private ArrayList messages;

	public Sender(Message m, InetAddress address, int port) {
		// Spara undan argumenten i klassens instansvariabler
		messages = new ArrayList<Message>();
		messages.add(m);
		this.adr = address;
		this.port = port;
		thread.start();	// Starta denna tråden
	}
	
	public Sender(Message m, String toUser, int port) {
		// Hämta mottagarens IP-adress och låt annan konstruktor göra resten av jobbet
		this(m, Server.onlineUsers.getUserAddress(toUser), port);
	}
	public Sender(Message m, String toUser) {
		// Hämta mottagarens IP-adress och låt annan konstruktor göra resten av jobbet
		this(m, Server.onlineUsers.getUserAddress(toUser), 4043);
	}

	public Sender(Message m, InetAddress address) {
		// Sätt port till 6789 och låt annan konstruktor göra resten av jobbet
		this(m, address, 4043);
	}
	/**
	 * Create new sender to send messages to a user
	 * @param messages the list of messages to send
	 * @param toUser The user to send messages to
	 */
	public Sender(ArrayList list, String toUser){
		this.messages = list;
		adr = Server.onlineUsers.getUserAddress(toUser);
		port = 4043;
		thread.start();
	}
	
	public Sender(ArrayList list, InetAddress toUser){
		this.messages = list;
		adr = toUser;
		port = 4043;
		thread.start();
	}

	@Override
	public void run() {
		try {
			// Kolla om vi har en address att skicka till innan vi skapar en anslutning
			if (adr == null) {
				if(messages.get(0) instanceof MapObject)return;
				for(Object m : messages){
					if(m instanceof RequestMessage) continue;
					Database.storeIntoBuffer((Message)m);
					Database.deleteFromTextMessages((TextMessage)m);
				}
				System.out.println("Mottagarens IP-adress är inte känd. ");	// Skriv ut att vi inte känner till mottagarens adress
				return;
			}
			// Skapa en socket för att kunna skicka meddelandet till mottagaren
			Socket rSocket = new Socket(adr, port);
			PrintWriter out = null;
			
			if(messages!=null){
				for(Object o : messages){	
					Gson gson = new Gson();
					String send = o.getClass().getName()+"\r\n";
					send +=	gson.toJson(o);
					out = new PrintWriter(rSocket.getOutputStream(), true);
					out.println(send);
				}
			}
		
			// Skriv ut vilken sorts meddelande som har skickats
			//System.out.println("["+rSocket.getInetAddress().getHostAddress()+"] << " + messages.get(0).getType().toString() + " har vidarebefordats till " 
			//+ messages.get(0).getDestUser());
			if(out != null)
				out.close();
			rSocket.close();

		} catch (IOException e) {
			if(messages.get(0) instanceof MapObject)return;
			// Logga ut denna användaren om 
			LoginManager.logoutUser(((Message) messages.get(0)).getDestUser());
			for(Object m : messages){
				if(m instanceof RequestMessage) continue;
				Database.storeIntoBuffer((Message)m);
				Database.deleteFromTextMessages((TextMessage)m);
			}
			// Mottagaren är inte online
			// Buffra meddelandet
			e.printStackTrace();
		}
	}
}
