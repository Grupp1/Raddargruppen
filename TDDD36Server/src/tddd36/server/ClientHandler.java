/*
 * ANVÄND INTE DENNA KLASSEN LÄNGRE
 * /

/* package tddd36.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import raddar.enums.MessagePriority;
import raddar.enums.MessageType;
import raddar.enums.NotificationType;

public class ClientHandler implements Runnable {
	
	private Thread clientThread = new Thread(this);
	
	private Socket so;
	private BufferedReader in;
	
	public ClientHandler(Socket clientSocket) {
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
	 * I denna metoden associerar eller avassocierar vi anv�ndare med IP-addresser
	 */ /*
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
					// Spara anv�ndaren och dennes IP-address (skriv �ver eventuell gammal IP-address)
					Server.onlineUsers.addUser(fromUser, so.getInetAddress());
					break;
				case DISCONNECT:
					// Ta bort anv�ndaren och dennes IP-address
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
	 * Ta emot ett textmeddelande. Denna metod l�r utvecklas mer sen f�r att 
	 * vidarebefordra meddelandet till en mottagarklient.
	 */ /* 
	private void handleTextMessage() {
		try {
			System.out.println("["+so.getInetAddress().getHostAddress()+"] >> Reading text message. ");
			
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
			tm.prepend("Borche (OK): ");
			
			// S�tt datum och �mnesrad
			tm.setDate(date);
			tm.setSubject(subject);
			
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
									
			System.out.println("["+so.getInetAddress().getHostAddress()+"] ** Connection closed. ");
			
		} catch (IOException ie) {
			ie.printStackTrace();
		}
	}
	
	/*
	 * To be implemented
	 */ /* 
	private void handleImageMessage() {
		
	}
	
	private String getAttrValue(String str) {
		StringBuilder sb = new StringBuilder("");
		String[] parts = str.split(" ");
		for (int i = 1; i < parts.length; i++) 
			sb.append(parts[i]);
		
		return sb.toString();
	}

}*/
