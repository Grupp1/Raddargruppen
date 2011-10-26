package tddd36.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import raddar.enums.MessagePriority;
import raddar.enums.MessageType;

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
			
			// F�r att skicka data till klienten
			//out = new PrintWriter(so.getOutputStream(), true);
			
			// L�s in vilken typ av meddelande som klienten skickar
			String msgType = in.readLine();
			String[] parts = msgType.split(" ");
			String t = parts[1];
			
			// Konvertera fr�n str�ng till MessageType
			MessageType type = MessageType.convert(t);
			
			// Kontroll-sats som, beroende p� vilken typ som l�sts in, ser till att resterande del av
			// meddelandet som klienten har skickat blir inl�st p� korrekt s�tt
			switch (type) {
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
	
	/*
	 * Ta emot ett textmeddelande. Denna metod l�r utvecklas mer sen f�r att 
	 * vidarebefordra meddelandet till en mottagarklient.
	 */
	private void handleTextMessage() {
		try {
			System.out.println("["+so.getInetAddress().getHostAddress()+"] >> Reading text message. ");
			
			// L�s in v�rden fr�n headern
			String priority = in.readLine().split(" ")[1];
			String fromUser = in.readLine().split(" ")[1];
			String toUser = in.readLine().split(" ")[1];
			
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
			
			// Extrahera mottagarens IP address (som �n s� l�nge alltid �r samma som s�ndarens)
			// och skapa en ny socket med denna IP address
			Socket forward = new Socket(so.getInetAddress(), 6789);
			
			// Ny PrintWriter f�r mottagarens socket
			PrintWriter fOut = new PrintWriter(forward.getOutputStream(), true);
			
			
			// Formatera och vidarebefordra meddelandet
			fOut.println(tm.getFormattedMessage());
			
			// Formatera textmeddelandet och skicka tillbaka det till klienten
			// out.println(tm.getFormattedMessage());
			
			fOut.close();
			forward.close();
			
			System.out.println("["+so.getInetAddress().getHostAddress()+"] << Replying with altered text message. ");
						
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
