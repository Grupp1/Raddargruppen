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
	 * H�rdkoda denna boolean true om klienten inte ska kontakta servern f�r inloggning
	 */
	public boolean debugMode = false;

	/**
	 * Verifierar att username och password �r giltiga. Denna metoden kommer att
	 * f�rs�ker verifiera med servern. Om klienten inte f�r kontakt med servern
	 * s� kollar den lokalt i cachen om det finns n�gon entry sparad.
	 * 
	 * @param username
	 *            Anv�ndarnamnet
	 * @param password
	 *            L�senordet
	 * @return true om verifieringen g�r bra, false annars
	 */
	public void evaluate(String username, String password) {
		try {
			// Skapa socket som anv�nds f�r att skicka NotificationMessage
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

			// L�s in salt fr�n server
			String salt = br.readLine();

			// Anv�nd saltet vi fick f�r att salta och kryptera l�senordet
			password = Encryption.encrypt(password, salt);

			// Skapa msg med anv�ndarnamn och krypterat l�senord
			NotificationMessage nm = new NotificationMessage(username,
					NotificationType.CONNECT, password);
			send = nm.getClass().getName() + "\r\n";
			gg = new Gson().toJson(nm);
			send += gg;
			
			// Skicka det saltade och krypterade l�senordet
			pw.println(send);

			// L�s in ett svar fr�n servern via SAMMA socket
			String response = br.readLine();

			// St�ng ner str�mmar och socket
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
			// Om servern inte kan n�s, kolla om vi har en f�rs�kande tr�d redan
			// ...har vi en f�rs�kande tr�d inneb�r det att vi redan �r inloggade lokalt
			// och d� returnerar vi h�r, annars loggar vi in lokalt
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
	 * Denna metoden kollar lokalt i cachen om anv�ndaren finns sparad i cachen
	 * och f�rs�ker i s�dana fall verifiera inmatade uppgifter emot dessa.
	 * 
	 * @param username
	 *            Anv�ndarnamnet
	 * @param password
	 *            L�senordet
	 * @return true om verifieringen g�r bra, false annars
	 */
	private LoginResponse evaluateLocally(String username, String password) {
		/*
		 * H�mta anv�ndarens salt s� att encrypt() kan hasha korrekt String salt
		 * = ClientDatabaseManager.getSalt(username); password =
		 * Encryption.encrypt(password, salt);
		 */
		String cachedPassword = passwordCache.get(username);
		if (cachedPassword == null)
			return LoginResponse.NO_CONNECTION;
		if (password.equals(cachedPassword)) {
			/*
			 * StubbornLoginThread f�rs�ker logga in mot servern med j�mna
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
		//Skicka alla medelanden som buffrats och t�m sedan buffern
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
	 * Privat klass som f�rs�ker att logga in emot servern med j�mna mellanrum
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
					// V�nta tv� minuter mellan varje f�rs�k
					Thread.sleep(1000 * 10);

				} catch (InterruptedException e) {
					Log.d("LoginManager.java", "evaluateLocally FAILADE!!");
					continue;
				}
			}
		}

	}

}
