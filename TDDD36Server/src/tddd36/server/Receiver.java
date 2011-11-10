package tddd36.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import raddar.enums.NotificationType;

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
			System.out.println("["+so.getInetAddress().getHostAddress()+"] ** Connection established. ");

			// F�r att l�sa inkommande data fr�n klienten
			in = new BufferedReader(new InputStreamReader(so.getInputStream()));
			
			Class c= null ;
			try {
				c = Class.forName(in.readLine());
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			String temp = in.readLine();
			
			Message m = new Gson().fromJson(temp, c);

			// Kontroll-sats som, beroende p� vilken typ som l�sts in, ser till att resterande del av
			// meddelandet som klienten har skickat blir inl�st p� korrekt s�tt
			switch (m.getType()) {
			case NOTIFICATION:
				handleNotification((NotificationMessage) m);
				break;
			case TEXT:
				Database.storeTextMessage((TextMessage)m);
				new Sender(m, m.getDestUser(), 6789);
				break;
			case IMAGE:
				handleImageMessage();
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
	 * To be implemented
	 */
	private void handleImageMessage() {

	}
	/*
	 * Denna funktionen anv�nds f�r att l�sa in en rad och filtrera bort attributtaggen
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
