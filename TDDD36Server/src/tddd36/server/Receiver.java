package tddd36.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

import raddar.enums.NotificationType;
import raddar.models.MapObjectMessage;
import raddar.models.Message;
import raddar.models.NotificationMessage;
import raddar.models.RequestMessage;
import raddar.models.TextMessage;

import com.google.gson.Gson;


/**
 * Denna klass b�r anv�ndas f�r att ta emot ett godtyckligt meddelande.
 * Skapa en instans av denna t ex n�r accept() (i klassen ServerSocket)
 * returnerar en instans av klassen Socket och skicka den returnerade
 * socketen som argument till denna klassen
 * 
 * @author andbo265
 *
 */


public class Receiver implements Runnable {

	private Thread clientThread = new Thread(this);

	private Socket so;
	private BufferedReader in;

	public Receiver(Socket clientSocket) {
		so = clientSocket;
		clientThread.start();
	}

	@Override
	public void run() {
		try {
			// F�r att l�sa inkommande data fr�n klienten
			in = new BufferedReader(new InputStreamReader(so.getInputStream()));
			Class c= null ;
			try {
				c = Class.forName(in.readLine());
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}

			String temp = in.readLine();
			System.out.println(temp);
			
			Message m = new Gson().fromJson(temp, c);
			// if message
			// Kontroll-sats som, beroende p� vilken typ som l�sts in, ser till att resterande del av
			// meddelandet som klienten har skickat blir inl�st p� korrekt s�tt
			switch (m.getType()) {
			case PROBE:
				Server.onlineUsers.confirmedProbeMessage(m.getSrcUser(), so.getInetAddress());
				break;
			case SOS:
				broadcast(m);
				break;
			case NOTIFICATION:
				handleNotification((NotificationMessage) m);
				break;
			case TEXT:
				Database.storeTextMessage((TextMessage) m);
				new Sender(m, m.getDestUser());
				break;
			case IMAGE:
				new Sender(m, m.getDestUser());
				break;
			case REQUEST:
				handleRequest((RequestMessage) m);
				break;
			case MAPOBJECT:
				handleMapObjectMessage((MapObjectMessage) m);
				broadcast(m);
				break;
			default:
				System.out.println("Received message has unknown type. Discarding... ");

			}
			//			
			//	so.close();
		} catch (IOException ie) {
			ie.printStackTrace();
		}

	}

	/*
	 * I denna metoden associerar och avassocierar vi anv�ndare med IP-addresser
	 * Klienterna b�r skicka ett NotificationMessage av typen CONNECT n�r de loggar
	 * online, samt ett NotificationMessage av typen DISCONNECT n�r de loggar off.
	 */
	private void handleNotification(NotificationMessage nm) {
		// Kolla vilken sorts notification vi har att g�ra med
		NotificationType nt = nm.getNotification();
		switch (nt) {
		case CONNECT:
			// Behandla loginf�rs�ket
			LoginManager.evaluateUser(nm.getSrcUser(), nm.getPassword(), so);
			break;
		case DISCONNECT:
			// Behandla logoutf�rs�ket
			LoginManager.logoutUser(nm.getSrcUser());
			break;
		default:
			// H�r hamnar vi om n�got g�tt fel i formatteringen eller inl�sandet av meddelandet
			System.out.println("Unknown NotificationType... ");
		}
	}

	/*
	 * Broadcasta ett meddelande m till alla i online-listan
	 */
	private void broadcast(Message m) {
		InetAddress srcAdr = Server.onlineUsers.getUserAddress(m.getSrcUser());
		for (InetAddress adr: Server.onlineUsers.getAllAssociations().values()){
			if(adr == srcAdr) continue;
			new Sender(m, adr, 4043);
		}

	}
	
	private void handleMapObjectMessage(MapObjectMessage mo){
		switch(mo.getMapOperation()){
		case ADD:
			System.out.println("handleMapObjectMessage ADD");
			
			if(Database.addMapObject(mo))
				broadcast(mo);
			break;
		case REMOVE:
			System.out.println("handleMapObjectMessage REMOVE");
			broadcast(mo);
			Database.removeMapObject(mo.getId());
			break;
		case UPDATE:
			System.out.println("handleMapObjectMessage UPDATE");
			broadcast(mo);
			Database.updateMapObject(mo);
			break;
		default:
			System.out.println("Ok�nd MapOperation");
		}	

	}

	/**
	 * Handles the request
	 * @param rm The recived requestMessage
	 */
	private void handleRequest(RequestMessage rm){
		switch(rm.getRequestType()){
		case MESSAGE:
			ArrayList<Message> messages = Database.retrieveAllTextMessagesTo(rm.getSrcUser());
			messages.add(0,rm);
			new Sender(messages,rm.getSrcUser());
			break;
		case BUFFERED_MESSAGE:
			ArrayList<Message> list = Database.retrieveAllBufferedMessagesTo(rm.getSrcUser());
			for(Message m: list){
				Database.storeTextMessage((TextMessage)m);
			}
			new Sender(list,rm.getSrcUser());
			Database.deleteFromBuffer(rm.getSrcUser());
			break;
		case SALT:
			String salt = Database.getSalt(rm.getSrcUser());
			try {
				// Skicka saltet till klienten som requestar det
				new PrintWriter(so.getOutputStream(), true).println(salt);
				Class c= null ;
				try {
					c = Class.forName(in.readLine());
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				String temp = in.readLine();
				Message m = new Gson().fromJson(temp, c);
				handleNotification((NotificationMessage) m);
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case CONTACTS:
			//svarar p� request om att h�mta alla kontakter som arraylist<message> 
			ArrayList<Message> contactMessage = Database.retrieveAllUsers();
			contactMessage.add(0,rm);
			new Sender(contactMessage, rm.getSrcUser());
			break;
		case MAP_OBJECTS:
			ArrayList<Message> mapObjectMessages = Database.retrieveAllMapObjects();
			new Sender(mapObjectMessages, rm.getSrcUser());
			break;
		default:
			System.out.println("Ok�nd RequestType");
		}

	}
}
