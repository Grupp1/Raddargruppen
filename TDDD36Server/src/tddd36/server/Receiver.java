package tddd36.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import raddar.enums.MessagePriority;
import raddar.enums.MessageType;
import raddar.enums.NotificationType;


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
						
			// Läs in vilken typ av meddelande som klienten skickar
			String t = in.readLine().split(" ")[1];
			
			// Konvertera från sträng till MessageType
			MessageType type = MessageType.convert(t);
			
			// Kontroll-sats som, beroende på vilken typ som lästs in, ser till att resterande del av
			// meddelandet som klienten har skickat blir inläst på korrekt sätt
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
	 * I denna metoden associerar och avassocierar vi användare med IP-addresser
	 * Klienterna bör skicka ett NotificationMessage av typen CONNECT när de loggar
	 * online, samt ett NotificationMessage av typen DISCONNECT när de loggar off.
	 */
	private void handleNotification() {
		try {
			// Read from who the notification is from
			String fromUser = in.readLine().split(" ")[1];
			
			// Read in the notification itself
			String notification = in.readLine().split(" ")[1];
			
			// Convert the notification from String to NotificationType
			NotificationType nt = NotificationType.convert(notification);
			
			switch (nt) {
				case CONNECT:
					// Spara användaren och dennes IP-address (skriv över eventuell gammal IP-address)
					System.out.println(fromUser + " is now associated with " + so.getInetAddress().getHostAddress());
					Server.onlineUsers.addUser(fromUser, so.getInetAddress());
					break;
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
	
	/*
	 * Ta emot ett textmeddelande och skapa en Sender som ser till att
	 * det skickas vidare till rätt mottagare
	 */
	private void handleTextMessage() {
		try {
			System.out.println("["+so.getInetAddress().getHostAddress()+"] >> text/plain message has been received. ");
			
			// Läs in värden från headern
			String priority = in.readLine().split(" ")[1];
			String fromUser = in.readLine().split(" ")[1];
			String toUser = in.readLine().split(" ")[1];
			String date = getAttrValue(in.readLine());
			String subject = getAttrValue(in.readLine());
			
			// Skippa den tomma raden som alla HTTP-formaterade meddelanden har
			in.readLine();
			
			// Läs in meddelandets data/text
			String data = "";
			while (in.ready())
				data += in.readLine();
			
			// Skapa ett nytt TextMessage med inlästa värden
			TextMessage tm = new TextMessage(MessageType.TEXT, fromUser, toUser, MessagePriority.convert(priority), data);
			
			// Lägg till lite text så att klienten kan se att denna testserver fungerar
			//tm.prepend("Borche (OK): ");
			
			// Sätt datum och ämnesrad
			tm.setDate(date);
			tm.setSubject(subject);

			// Skapa en Sender som tar hand om att skicka vidare meddelandet
			new Sender(tm, toUser, 6789);
			
			/*
			// Hämta mottagarens IP-address från serverns lista 
			InetAddress address = Server.onlineUsers.getUserAddress(toUser);
			
			if (address == null) {
				// Kolla om användaren existerar om JA, buffra, annars discard.
				// Användaren är offline
				// Buffra meddelandet (to be implemented...)
				return;
			}
				
			// Skapa en socket med mottagarens address och den porten som klienten
			// ligger och lyssnar på (hårdkodat på klienterna är 6789 när detta skrevs).
			Socket forward = new Socket(address, 6789);			
			
			// Ny PrintWriter för mottagarens socket
			PrintWriter fOut = new PrintWriter(forward.getOutputStream(), true);
			
			
			// Formatera och vidarebefordra meddelandet
			fOut.println(tm.getFormattedMessage());
			
			System.out.println("["+forward.getInetAddress().getHostAddress()+"] << Forwarding text message. ");
			
			// Stäng ner
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
