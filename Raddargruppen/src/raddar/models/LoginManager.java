package raddar.models;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;

import raddar.controllers.DatabaseController;
import raddar.controllers.Sender;
import raddar.enums.LoginResponse;
import raddar.enums.MessageType;
import raddar.enums.NotificationType;
import raddar.enums.RequestType;
import raddar.enums.ServerInfo;
import android.util.Log;

import com.google.gson.Gson;

public class LoginManager extends Observable {

	private static HashMap<String, String> passwordCache = new HashMap<String, String>();

	private StubbornLoginThread s = null;
	private LoginResponse logIn = LoginResponse.NO_SUCH_USER_OR_PASSWORD;
	/**
	 * Hårdkoda denna boolean true om klienten inte ska kontakta servern för inloggning
	 */
	public boolean debugMode = false;

	/**
	 * Verifierar att username och password är giltiga. Denna metoden kommer att
	 * försöker verifiera med servern. Om klienten inte får kontakt med servern
	 * så kollar den lokalt i cachen om det finns någon entry sparad.
	 * 
	 * @param username
	 *            Användarnamnet
	 * @param password
	 *            Lösenordet
	 * @return true om verifieringen går bra, false annars
	 */
	public void evaluate(String username, String password) {
		try {
			// Skapa socket som används för att skicka NotificationMessage
			Socket so = new Socket();
			// Socket so = new
			// Socket(InetAddress.getByName(ServerInfo.SERVER_IP),
			// ServerInfo.SERVER_PORT);
			InetAddress addr = InetAddress.getByName(ServerInfo.SERVER_IP);
			int port = ServerInfo.SERVER_PORT;
			SocketAddress sockAddr = new InetSocketAddress(addr, port);
			int TIME_OUT = 5000;
			so.connect(sockAddr, TIME_OUT);

			PrintWriter pw = new PrintWriter(so.getOutputStream(), true);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					so.getInputStream()));

			RequestMessage rm = new RequestMessage(RequestType.SALT);
			rm.setSrcUser(username);
			String send = rm.getClass().getName() + "\r\n";
			String gg = new Gson().toJson(rm);
			send += gg;
			// Skicka SALT-request
			pw.println(send);

			// Läs in salt från server
			String salt = br.readLine();

			// Använd saltet vi fick för att salta och kryptera lösenordet
			password = Encryption.encrypt(password, salt);

			// Skapa msg med användarnamn och krypterat lösenord
			NotificationMessage nm = new NotificationMessage(username,
					NotificationType.CONNECT, password);
			send = nm.getClass().getName() + "\r\n";
			gg = new Gson().toJson(nm);
			send += gg;
			
			// Skicka det saltade och krypterade lösenordet
			pw.println(send);

			// Läs in ett svar från servern via SAMMA socket
			String response = br.readLine();

			// Stäng ner strömmar och socket
			pw.close();
			br.close();
			so.close();
			
			if (response.equals("OK")) {
				
				logIn = LoginResponse.ACCEPTED;
				sendBufferedMessages();
				s = null;
			}
			else if(response.equals("OK_FORCE_LOGOUT")){
				logIn = LoginResponse.USER_ALREADY_LOGGED_IN;
				s = null;
			}
		} catch (IOException e) {
			Log.d("NotificationMessage", "Server connection failed");
			// Om servern inte kan nås, kolla om vi har en försökande tråd redan
			// ...har vi en försökande tråd innebär det att vi redan är inloggade lokalt
			// och då returnerar vi här, annars loggar vi in lokalt
			if (s == null)
				logIn = evaluateLocally(username, password);
			else
				return;
		}
		if(debugMode){
			logIn = LoginResponse.ACCEPTED_NO_CONNECTION;
		}
		setChanged();
		notifyObservers(logIn);
	}

	/**
	 * Denna metoden kollar lokalt i cachen om användaren finns sparad i cachen
	 * och försöker i sådana fall verifiera inmatade uppgifter emot dessa.
	 * 
	 * @param username
	 *            Användarnamnet
	 * @param password
	 *            Lösenordet
	 * @return true om verifieringen går bra, false annars
	 */
	private LoginResponse evaluateLocally(String username, String password) {
		/*
		 * Hämta användarens salt så att encrypt() kan hasha korrekt String salt
		 * = ClientDatabaseManager.getSalt(username); password =
		 * Encryption.encrypt(password, salt);
		 */
		String cachedPassword = passwordCache.get(username);
		if (cachedPassword == null)
			return LoginResponse.NO_CONNECTION;
		if (password.equals(cachedPassword)) {
			/*
			 * StubbornLoginThread försöker logga in mot servern med jämna
			 * mellanrum
			 */
			s = new StubbornLoginThread(username, password);
			return LoginResponse.ACCEPTED_NO_CONNECTION;
		}
		return LoginResponse.NO_CONNECTION;
	}

	public static String cache(String username, String password) {
		return passwordCache.put(username, password);
	}

	public static String removeCache(String username) {
		return passwordCache.remove(username);
	}
	private void sendBufferedMessages() throws UnknownHostException{
		//Skicka alla medelanden som buffrats och töm sedan buffern
		ArrayList<String> bufferedMessages = new ArrayList<String>();
		bufferedMessages = DatabaseController.db.getAllRowsAsArrays("bufferedMessage");
		if(bufferedMessages != null){

			
			for(String gsonString: bufferedMessages){
				new Sender(gsonString);
				Log.d("GSONSTRING",gsonString);
				DatabaseController.db.deleteBufferedMesageRow(gsonString);
			}
		}
	}
	
	/*
	 * Privat klass som försöker att logga in emot servern med jämna mellanrum
	 */
	private class StubbornLoginThread implements Runnable {

		private String username, password;
		private Thread thrd = new Thread(this);

		StubbornLoginThread(String username, String password) {
			this.username = username;
			this.password = password;
			thrd.start();
		}

		public void run() {
			while (true) {
				try {
					if (s == null)
						break;
					evaluate(username, password);
					if (s == null)
						break;
					// Vänta två minuter mellan varje försök
					Thread.sleep(1000 * 10);

				} catch (InterruptedException e) {
					Log.d("LoginManager.java", "evaluateLocally FAILADE!!");
					continue;
				}
			}
		}

	}

}
