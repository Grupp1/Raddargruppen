package tddd36.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.util.ArrayList;

import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import raddar.models.Message;
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
	private int port;	// Porten som mottagaren lyssnar p�
	private ArrayList<Message> messages;

	public Sender(Message m, InetAddress address, int port) {
		// Spara undan argumenten i klassens instansvariabler
		messages = new ArrayList<Message>();
		messages.add(m);
		this.adr = address;
		this.port = port;
		thread.start();	// Starta denna tr�den
	}

	public Sender(Message m, String toUser, int port) {
		// H�mta mottagarens IP-adress och l�t annan konstruktor g�ra resten av jobbet
		this(m, Server.onlineUsers.getUserAddress(toUser), port);
	}
	public Sender(Message m, String toUser) {
		// H�mta mottagarens IP-adress och l�t annan konstruktor g�ra resten av jobbet
		this(m, Server.onlineUsers.getUserAddress(toUser), 4043);
	}

	public Sender(Message m, InetAddress address) {
		// S�tt port till 6789 och l�t annan konstruktor g�ra resten av jobbet
		this(m, address, 4043);
	}
	/**
	 * Create new sender to send messages to a user
	 * @param messages the list of messages to send
	 * @param toUser The user to send messages to
	 */
	public Sender(ArrayList<Message> list, String toUser){
		this.messages = list;
		adr = Server.onlineUsers.getUserAddress(toUser);
		port = 4043;
		thread.start();
	}

	
	/**
	 * Skickar ett meddeladne till alla som �r online.
	 * @param m meddeladnen som ska skickas.
	 */
	public static void broadcast(Message m) {
		for (InetAddress adr: Server.onlineUsers.getAllAssociations().values()){
			//if(adr == srcAdr) continue; //Beh�vs inte i sender?
			new Sender(m, adr, 4043);
		}

	}
	
	public Sender(ArrayList<Message> list, InetAddress toUser){
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

				for(Message m : messages){
					if(m instanceof TextMessage){
						Database.storeIntoBuffer((Message)m);
						Database.deleteFromTextMessages((TextMessage)m);
					}
				}
				System.out.println("Mottagarens IP-adress �r inte k�nd. Buffrar meddelandet... ");	// Skriv ut att vi inte k�nner till mottagarens adress
				return;
			}
			// Skapa en socket f�r att kunna skicka meddelandet till mottagaren
			//Socket rSocket = new Socket(adr, port);
			SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
			SSLSocket sslsocket = (SSLSocket) sslsocketfactory.createSocket(adr, port);
			sslsocket.setEnabledCipherSuites(new String[] { "SSL_DH_anon_WITH_RC4_128_MD5" });
			SSLSession sslsession = sslsocket.getSession();
			PrintWriter out = null;

			if(messages!=null){
				for(Object o : messages){	
					Gson gson = new Gson();
					String send = o.getClass().getName()+"\r\n";
					send +=	gson.toJson(o);
					out = new PrintWriter(sslsocket.getOutputStream(), true);
					out.println(send);
				}
			}

			// Skriv ut vilken sorts meddelande som har skickats
			//System.out.println("["+rSocket.getInetAddress().getHostAddress()+"] << " + messages.get(0).getType().toString() + " har vidarebefordats till " 
			//+ messages.get(0).getDestUser());
			if(out != null)
				out.close();
			sslsocket.close();

		} catch (IOException e) {

			// Logga ut denna anv�ndaren om 
			if(messages.size() <= 0) return;
			LoginManager.logoutUser(((Message) messages.get(0)).getDestUser());
			for(Message m : messages){
				if(m instanceof TextMessage){
					Database.storeIntoBuffer((Message)m);
					Database.deleteFromTextMessages((TextMessage)m);
				}
			}
			// Mottagaren �r inte online
			// Buffra meddelandet
		}
	}
}
