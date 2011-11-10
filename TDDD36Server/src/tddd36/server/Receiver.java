package tddd36.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import raddar.enums.MessageType;
import raddar.enums.NotificationType;

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
			System.out.println("["+so.getInetAddress().getHostAddress()+"] ** Connection established. ");

			// För att läsa inkommande data från klienten
			in = new BufferedReader(new InputStreamReader(so.getInputStream()));
			Gson gson = new Gson();
			Class c= null ;
			try {
				c = Class.forName(in.readLine());
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			String temp = in.readLine();
			Message m = new Gson().fromJson(temp, c);
			//		so.close();
			// Läs in vilken typ av meddelande som klienten skickar
			String t = in.readLine().split(" ")[1];

			// Konvertera från sträng till MessageType
			MessageType type = MessageType.convert(t);

			// Kontroll-sats som, beroende på vilken typ som lästs in, ser till att resterande del av
			// meddelandet som klienten har skickat blir inläst på korrekt sätt
			switch (m.getType()) {
			case NOTIFICATION:
				handleNotification((NotificationMessage)m);
				break;
			case TEXT:
				new Sender(m, m.getDestUser(), 6789);
				//	handleTextMessage();
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
	 * I denna metoden associerar och avassocierar vi användare med IP-addresser
	 * Klienterna bör skicka ett NotificationMessage av typen CONNECT när de loggar
	 * online, samt ett NotificationMessage av typen DISCONNECT när de loggar off.
	 */
	private void handleNotification() {

		boolean passwordCheck = false;

		try {
			// Read from who the notification is from
			String fromUser = in.readLine().split(" ")[1];

			// Read the password
			String password = in.readLine().split(" ")[1];

			// Read in the notification itself
			String notification = in.readLine().split(" ")[1];

			// Convert the notification from String to NotificationType
			NotificationType nt = NotificationType.convert(notification);

			switch (nt) {
			case CONNECT:
				// Spara användaren och dennes IP-address (skriv över eventuell gammal IP-address)

				passwordCheck = Database.evalutateUser(fromUser, password);
				System.out.println("PASSWORD = "+passwordCheck);
				System.out.println("user " + fromUser);
				System.out.println("pass " + password);

				if (passwordCheck){			
					System.out.println(fromUser + " is now associated with " + so.getInetAddress().getHostAddress());
					Server.onlineUsers.addUser(fromUser, so.getInetAddress());

					PrintWriter pw = new PrintWriter(so.getOutputStream(), true);
					pw.println("OK");
				}
				else{
					System.out.println("Användarnamn eller lösenord fel");
					PrintWriter pw = new PrintWriter(so.getOutputStream(), true);
					pw.println("Not OK");
				}break;

			case DISCONNECT:
				// Ta bort användaren och dennes IP-address
				System.out.println(fromUser + " is no longer associated with " + so.getInetAddress().getHostAddress());
				Server.onlineUsers.removeUser(fromUser);
				break;
			default:
				// Här hamnar vi om något gått fel i formatteringen eler inläsandet av meddelandet
				System.out.println("Unknown NotificationType... ");
			}


		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void handleNotification(NotificationMessage m) {
		boolean passwordCheck = false;
		switch (m.getNotification()) {
		case CONNECT:
			passwordCheck = Database.evalutateUser(m.getSrcUser(), m.getPassword());
			System.out.println("PASSWORD = "+passwordCheck);
			System.out.println("user " + m.getSrcUser());
			System.out.println("pass " + m.getPassword());
			try{
				if (passwordCheck){	
					System.out.println(m.getSrcUser() + " is now associated with " + so.getInetAddress().getHostAddress());
					Server.onlineUsers.addUser(m.getSrcUser(), so.getInetAddress());

					PrintWriter pw;

					pw = new PrintWriter(so.getOutputStream(), true);



					pw.println("OK");
				}
				else{
					System.out.println("Användarnamn eller lösenord fel");
					PrintWriter pw = new PrintWriter(so.getOutputStream(), true);
					pw.println("Not OK");
				}break;

			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case DISCONNECT:
			// Ta bort användaren och dennes IP-address
			System.out.println(m.getSrcUser() + " is no longer associated with " + so.getInetAddress().getHostAddress());
			Server.onlineUsers.removeUser(m.getSrcUser());
			break;
		default:
			// Här hamnar vi om något gått fel i formatteringen eler inläsandet av meddelandet
			System.out.println("Unknown NotificationType... ");
		}
	}

	/*
	 * To be implemented
	 */
	private void handleImageMessage() {

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
