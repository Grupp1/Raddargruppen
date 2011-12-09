package tddd36.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.util.ArrayList;

import javax.net.ssl.SSLSocket;

import raddar.enums.MapOperation;
import raddar.enums.MessageType;
import raddar.enums.NotificationType;
import raddar.enums.OnlineOperation;
import raddar.enums.RequestType;
import raddar.models.MapObjectMessage;
import raddar.models.Message;
import raddar.models.NotificationMessage;
import raddar.models.OnlineUsersMessage;
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

	//Gamla inlogg
	//private Socket so;
	private BufferedReader in;

	//Nya ssl
	private SSLSocket so;

	public Receiver(SSLSocket clientSocket) {
		so = clientSocket;
		clientThread.start();
	}

	@Override
	public void run() {
		try {
			in = new BufferedReader(new InputStreamReader(so.getInputStream()));
			Class c= null ;
			try {
				String str = in.readLine();
				if(str == null) return;
				c = Class.forName(str);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			Message m = new Gson().fromJson(in.readLine(), c);
			
			MessageType mt = m.getType();
			if(mt!=MessageType.PROBE){
				System.out.println();
				System.out.println("Messagetype: "+mt.toString());
				System.out.println(m.getSrcUser()+" är online: "+Server.onlineUsers.isUserOnline(m.getSrcUser()));
			}
			
			//Det den h�r if-satsen g�r �t att unders�ka om anv�ndaren som skickade meddelandet �r online.
			//Om han inte �r det m�ste �r det enda meddelandena har f�r skicka notifications och requesta salt.
			//Om han g�r m�got annat loggas han ut.
			if(!Server.onlineUsers.isUserOnline(m.getSrcUser()) &&
					(m.getType() != MessageType.NOTIFICATION && !(m.getType() == MessageType.REQUEST &&
					((RequestMessage)m).getRequestType() == RequestType.SALT))){
				System.out.println("Not online");
				NotificationMessage nm = (new NotificationMessage("Server", NotificationType.DISCONNECT));
				nm.setData("Du är inte inloggad mot servern. Var v�nlig logga in igen.");
				new Sender(nm, so.getInetAddress());
				return;
			}

			// Kontroll-sats som, beroende p� vilken typ som l�sts in, ser till att resterande del av
			// meddelandet som klienten har skickat blir inl�st p� korrekt s�tt

			switch (mt) {
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
				Database.storeTextMessage((TextMessage)m);
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
		System.out.println("Notificationtype: "+nt);
		switch (nt) {
		case CONNECT:
			// Behandla loginf�rs�ket
			LoginManager.evaluateUser(nm.getSrcUser(), nm.getPassword(), so);
			break;
		case DISCONNECT:
			// Behandla logoutf�rs�ket
			if(so.getInetAddress().equals(Server.onlineUsers.getUserAddress(nm.getSrcUser())))
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
		MapOperation mapO = mo.getMapOperation();
		System.out.println("MapOperation: "+mapO.toString());
		switch(mapO){
		case ADD:
			//System.out.println("handleMapObjectMessage ADD");
			if(Database.addMapObject(mo))
				break;
		case REMOVE:
			//System.out.println("handleMapObjectMessage REMOVE");
			Database.removeMapObject(mo.getId());
			break;
		case UPDATE:
		case ALARM_ON:
		case ALARM_OFF:
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
		RequestType rt = rm.getRequestType();
		System.out.println("RequestType: "+rt);
		switch(rm.getRequestType()){
		case MESSAGE:
			ArrayList<Message> messages = Database.retrieveAllTextMessagesTo(rm.getSrcUser());
			messages.add(0,rm);
			new Sender(messages,rm.getSrcUser());
			break;
		case BUFFERED_MESSAGE:
			ArrayList<Message> buffered = Database.retrieveAllBufferedMessagesTo(rm.getSrcUser());
			for(Message m: buffered){
				Database.storeTextMessage((TextMessage)m);
			}
			new Sender(buffered,rm.getSrcUser());
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
		case ONLINE_CONTACTS:
			ArrayList<String> onlineUsersMessages = Associations.getOnlineUserNames();
			for(String onlineUser: onlineUsersMessages){
				OnlineUsersMessage onlineUsermessage  = new OnlineUsersMessage(OnlineOperation.ADD, onlineUser);
				new Sender(onlineUsermessage, rm.getSrcUser());
			}
			break;
		case NEW_LOGIN:
			ArrayList<Message> list =  Database.retrieveAllTextMessagesTo(rm.getSrcUser());
			ArrayList<Message> tempBuffered = Database.retrieveAllBufferedMessagesTo(rm.getSrcUser());
			for(Message m: tempBuffered){
				Database.storeTextMessage((TextMessage)m);
			}
			Database.deleteFromBuffer(rm.getSrcUser());
			list.addAll(tempBuffered);
			list.addAll(Database.retrieveAllMapObjects());
			ArrayList<String> temp1 = Associations.getOnlineUserNames();
			ArrayList<OnlineUsersMessage> temp2 = new ArrayList<OnlineUsersMessage>();
			for(String onlineUser: temp1){
				temp2.add(new OnlineUsersMessage(OnlineOperation.ADD, onlineUser));
			}
			list.addAll(temp2);
			list.addAll(Database.retrieveAllUsers());
			new Sender(list, rm.getSrcUser());
			break;
		default:
			System.out.println("Ok�nd RequestType");
		}
	}
}









