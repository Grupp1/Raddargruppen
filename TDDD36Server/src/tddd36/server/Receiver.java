package tddd36.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import raddar.enums.MessagePriority;
import raddar.enums.MessageType;
import raddar.enums.NotificationType;


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
						
			// L�s in vilken typ av meddelande som klienten skickar
			String t = in.readLine().split(" ")[1];
			
			// Konvertera fr�n str�ng till MessageType
			MessageType type = MessageType.convert(t);
			
			// Kontroll-sats som, beroende p� vilken typ som l�sts in, ser till att resterande del av
			// meddelandet som klienten har skickat blir inl�st p� korrekt s�tt
			switch (type) {
				case NOTIFICATION:
					handleNotification();
					break;
				case TEXT:
					handleTextMessage();
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
					// Spara anv�ndaren och dennes IP-address (skriv �ver eventuell gammal IP-address)
					
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
						System.out.println("Anv�ndarnamn eller l�senord fel");
						PrintWriter pw = new PrintWriter(so.getOutputStream(), true);
						pw.println("Not OK");
					}break;
					
				case DISCONNECT:
					// Ta bort anv�ndaren och dennes IP-address
					System.out.println(fromUser + " is no longer associated with " + so.getInetAddress().getHostAddress());
					Server.onlineUsers.removeUser(fromUser);
					break;
				default:
					// H�r hamnar vi om n�got g�tt fel i formatteringen eler inl�sandet av meddelandet
					System.out.println("Unknown NotificationType... ");
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Ta emot ett textmeddelande och skapa en Sender som ser till att
	 * det skickas vidare till r�tt mottagare
	 */
	private void handleTextMessage() {
		try {
			System.out.println("["+so.getInetAddress().getHostAddress()+"] >> text/plain message has been received. ");
			
			// L�s in v�rden fr�n headern
			String priority = in.readLine().split(" ")[1];
			String fromUser = in.readLine().split(" ")[1];
			String toUser = in.readLine().split(" ")[1];
			String date = getAttrValue(in.readLine());
			String subject = getAttrValue(in.readLine());
			
			// Skippa den tomma raden som alla HTTP-formaterade meddelanden har
			in.readLine();
			
			// L�s in meddelandets data/text
			String data = "";
			while (in.ready())
				data += in.readLine();
			
			// Skapa ett nytt TextMessage med inl�sta v�rden
			TextMessage tm = new TextMessage(MessageType.TEXT, fromUser, toUser, MessagePriority.convert(priority), data);
			
			// L�gg till lite text s� att klienten kan se att denna testserver fungerar
			//tm.prepend("Borche (OK): ");
			
			// S�tt datum och �mnesrad
			tm.setDate(date);
			tm.setSubject(subject);

			// Skapa en Sender som tar hand om att skicka vidare meddelandet
			new Sender(tm, toUser, 6789);
			
			/*
			// H�mta mottagarens IP-address fr�n serverns lista 
			InetAddress address = Server.onlineUsers.getUserAddress(toUser);
			
			if (address == null) {
				// Kolla om anv�ndaren existerar om JA, buffra, annars discard.
				// Anv�ndaren �r offline
				// Buffra meddelandet (to be implemented...)
				return;
			}
				
			// Skapa en socket med mottagarens address och den porten som klienten
			// ligger och lyssnar p� (h�rdkodat p� klienterna �r 6789 n�r detta skrevs).
			Socket forward = new Socket(address, 6789);			
			
			// Ny PrintWriter f�r mottagarens socket
			PrintWriter fOut = new PrintWriter(forward.getOutputStream(), true);
			
			
			// Formatera och vidarebefordra meddelandet
			fOut.println(tm.getFormattedMessage());
			
			System.out.println("["+forward.getInetAddress().getHostAddress()+"] << Forwarding text message. ");
			
			// St�ng ner
			fOut.close();
			forward.close();
									
			System.out.println("["+so.getInetAddress().getHostAddress()+"] ** Connection closed. ");*/
			
		} catch (IOException ie) {
			ie.printStackTrace();
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
