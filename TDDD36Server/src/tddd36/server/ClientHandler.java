package tddd36.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
			
			// För att läsa inkommande data från klienten
			in = new BufferedReader(new InputStreamReader(so.getInputStream()));
			
			// För att skicka data till klienten
			//out = new PrintWriter(so.getOutputStream(), true);
			
			// Läs in vilken typ av meddelande som klienten skickar
			String msgType = in.readLine();
			String[] parts = msgType.split(" ");
			String t = parts[1];
			
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
					System.out.println("Received message has unknown type. Aborting... ");			
			}
			
		} catch (IOException ie) {
			ie.printStackTrace();
		}
		
	}
	
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
					Server.onlineUsers.addUser(fromUser, so.getInetAddress());
					break;
				case DISCONNECT:
					// Ta bort användaren och dennes IP-address
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
	 * Ta emot ett textmeddelande. Denna metod lär utvecklas mer sen för att 
	 * vidarebefordra meddelandet till en mottagarklient.
	 */
	private void handleTextMessage() {
		try {
			System.out.println("["+so.getInetAddress().getHostAddress()+"] >> Reading text message. ");
			
			// Läs in värden från headern
			String priority = in.readLine().split(" ")[1];
			String fromUser = in.readLine().split(" ")[1];
			String toUser = in.readLine().split(" ")[1];
			
			// Skippa den tomma raden som alla HTTP-formaterade meddelanden har
			in.readLine();
			
			// Läs in meddelandets data/text
			String data = "";
			while (in.ready())
				data += in.readLine();
			
			// Skapa ett nytt TextMessage med inlästa värden
			TextMessage tm = new TextMessage(MessageType.TEXT, fromUser, toUser, MessagePriority.convert(priority), data);
			
			// Lägg till lite text så att klienten kan se att denna testserver fungerar
			tm.prepend("Borche (OK): ");
			
			// Extrahera mottagarens IP address (som än så länge alltid är samma som sändarens)
			// och skapa en ny socket med denna IP address
			Socket forward = new Socket(so.getInetAddress(), 6789);
			
			// Ny PrintWriter för mottagarens socket
			PrintWriter fOut = new PrintWriter(forward.getOutputStream(), true);
			
			
			// Formatera och vidarebefordra meddelandet
			fOut.println(tm.getFormattedMessage());
			
			System.out.println("["+so.getInetAddress().getHostAddress()+"] << Replying with altered text message. ");
			
			// Stäng ner
			fOut.close();
			forward.close();
									
			System.out.println("["+so.getInetAddress().getHostAddress()+"] ** Connection closed. ");
			
		} catch (IOException ie) {
			ie.printStackTrace();
		}
	}
	
	/*
	 * To be implemented
	 */
	private void handleImageMessage() {
		
	}

}
