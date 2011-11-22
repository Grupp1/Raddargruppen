package tddd36.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

import raddar.enums.NotificationType;
import raddar.models.Message;
import raddar.models.NotificationMessage;
import raddar.models.RequestMessage;

import com.google.gson.Gson;


/**
 * Denna klass bör användas för att ta emot ett godtyckligt meddelande.
 * Skapa en instans av denna t ex när accept() (i klassen ServerSocket)
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
			// För att läsa inkommande data från klienten
			in = new BufferedReader(new InputStreamReader(so.getInputStream()));
			Class c= null ;
			try {
				c = Class.forName(in.readLine());
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			String temp = in.readLine();
			Message m = new Gson().fromJson(temp, c);
			//		so.close();
			
			switch (m.getType()) {
			case NOTIFICATION:
				handleNotification((NotificationMessage) m);
				break;
			case TEXT:
				//Database.storeTextMessage((TextMessage)m);
				new Sender(m, m.getDestUser());
				break;
			case IMAGE:
				handleImageMessage();
				break;
			case REQUEST:
				handleRequest((RequestMessage) m);
				break;
			default:
				System.out.println("Received message has unknown type. Discarding... ");
			}

		} catch (IOException ie) {
			ie.printStackTrace();
		}

	}

	/*
	 * I denna metoden associerar och avassocierar vi användare med IP-addresser
	 * Klienterna bör skicka ett NotificationMessage av typen CONNECT när de loggar
	 * online, samt ett NotificationMessage av typen DISCONNECT när de loggar off.
	 */
	private void handleNotification(NotificationMessage nm) {
		// Kolla vilken sorts notification vi har att göra med
		NotificationType nt = nm.getNotification();
		switch (nt) {
			case CONNECT:
				// Behandla loginförsöket
				LoginManager.evaluateUser(nm.getSrcUser(), nm.getPassword(), so);
				break;
			case DISCONNECT:
				// Behandla logoutförsöket
				LoginManager.logoutUser(nm.getSrcUser());
				break;
			default:
				// Här hamnar vi om något gått fel i formatteringen eller inläsandet av meddelandet
				System.out.println("Unknown NotificationType... ");
		}
	}

	/*
	 * To be implemented
	 */
	private void handleImageMessage() {

	}
	/**
	 * Handles the request
	 * @param rm The recived requestMessage
	 */
	private void handleRequest(RequestMessage rm){
		switch(rm.getRequestType()){
		case MESSAGE:
			ArrayList<Message> messages =Database.retrieveAllTextMessagesTo(rm.getSrcUser());
			messages.add(0,rm);
			new Sender(messages,rm.getSrcUser());
			break;
		default:
			System.out.println("Unknown RequestType");
		}
	}
	/*
	 * Denna funktionen används för att läsa in en rad och filtrera bort attributtaggen
	 * 'Content-Type: text/plain' filtreras till exempel till text/plain
	 */
	
	private String getAttrValue(String str) {
		StringBuilder sb = new StringBuilder("");
		String[] parts = str.split(" ");
		for (int i = 1; i < parts.length; i++) 
			sb.append(parts[i]);

		return sb.toString();
	}

}
