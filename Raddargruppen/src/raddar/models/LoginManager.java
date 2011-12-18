package raddar.models;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;

import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import raddar.controllers.DatabaseController;
import raddar.controllers.Sender;
import raddar.controllers.SessionController;
import raddar.enums.ConnectionStatus;
import raddar.enums.LoginResponse;
import raddar.enums.NotificationType;
import raddar.enums.RequestType;
import raddar.enums.ServerInfo;
import android.util.Log;

import com.google.gson.Gson;

public class LoginManager extends Observable {

	private static HashMap<String, String> passwordCache = new HashMap<String, String>();

	private StubbornLoginThread s = null;
	private static boolean thread_is_active = false;
	private static boolean server_connection_achieved = false;
	private LoginResponse logIn = LoginResponse.NO_SUCH_USER_OR_PASSWORD;
	
	private static int nLoginThreads = 0;

	/**
	 * Verifierar att username och password �r giltiga. Denna metoden kommer att
	 * f�rs�ker verifiera med servern. Om klienten inte f�r kontakt med servern
	 * s� kollar den lokalt i cachen om det finns n�gon entry sparad.
	 * 
<<<<<<< HEAD
	 * @param username
	 *            Anv�ndarnamnet
=======
	 * @param userName
	 *            Anv�ndarnamnet
>>>>>>> cache
	 * @param password
	 *            L�senordet
	 * @return true om verifieringen g�r bra, false annars
	 */
	public void evaluate(String userName, String password, boolean passwordNotHashed) {
		try {
			server_connection_achieved = false;
			//nya ssl
			SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
			SSLSocket sslsocket = (SSLSocket) sslsocketfactory.createSocket();
			Log.d("ssl",sslsocket.getSupportedCipherSuites()[0]);
			
			InetAddress addr = InetAddress.getByName(ServerInfo.SERVER_IP);
			int port = ServerInfo.SERVER_PORT;
			SocketAddress sockAddr = new InetSocketAddress(addr, port);
			int TIME_OUT = 5000;
			sslsocket.connect(sockAddr, TIME_OUT);

			Log.d("LoginManager.java", "server_connection_achieved sätts nu till true...");
			server_connection_achieved = true;

			sslsocket.setEnabledCipherSuites(new String[] { "SSL_DH_anon_WITH_RC4_128_MD5" });
			// Initiera handskakningen
			SSLSession session = sslsocket.getSession();
			
			PrintWriter pw = new PrintWriter(sslsocket.getOutputStream(), true);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					sslsocket.getInputStream()));
			String send = "";
			String gg = "";
			String salt = "";
			if(passwordNotHashed){
				RequestMessage rm = new RequestMessage(RequestType.SALT);
				rm.setSrcUser(userName);
				send = rm.getClass().getName() + "\r\n";
				gg = new Gson().toJson(rm);
				send += gg;
				// Skicka SALT-request
				pw.println(send);

				// L�s in salt fr�n server
				salt = br.readLine();

				// Anv�nd saltet vi fick f�r att salta och kryptera l�senordet
				password = Encryption.encrypt(password, salt);
			}

			// Skapa msg med anv�ndarnamn och krypterat l�senord
			NotificationMessage nm = new NotificationMessage(userName,
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
			sslsocket.close();
			
			if (response.equals("OK")) {
				SessionController.setPassword(password);
				SessionController.setUserName(userName);
				if(SessionController.getSessionController()!=null)
					SessionController.getSessionController().changeConnectionStatus(ConnectionStatus.CONNECTED);
				logIn = LoginResponse.ACCEPTED;
				if(passwordNotHashed)
					cache(userName, password, salt);
				sendBufferedMessages();
			}
			else if(response.equals("OK_FORCE_LOGOUT")){
				SessionController.setPassword(password);
				SessionController.setUserName(userName);
				if(SessionController.getSessionController()!=null)
					SessionController.getSessionController().changeConnectionStatus(ConnectionStatus.CONNECTED);
				logIn = LoginResponse.USER_ALREADY_LOGGED_IN;
				if(passwordNotHashed)
					cache(userName, password, salt);
				sendBufferedMessages();
			}
		} catch (IOException e) {
			Log.d("LoginManager.java", "Socket mot server kunde inte skapas... "+e.toString());
			// Om servern inte kan n�s, kolla om vi har en f�rs�kande tr�d redan
			// ...har vi en f�rs�kande tr�d inneb�r det att vi redan �r inloggade lokalt
			// och d� returnerar vi h�r, annars loggar vi in lokalt
			if(!passwordNotHashed && thread_is_active == false){
				s = new StubbornLoginThread(userName, password);
				Log.d("LoginManager.java", "S har nu skapats i evaluate()");
				return;
			}
			else if (thread_is_active == false)
				logIn = evaluateLocally(userName, password);
			else
				return;
		}
		setChanged();
		notifyObservers(logIn);
	}
	public boolean isRunningStubornLoginThread(){
		return thread_is_active;
	}

	/**
	 * Denna metoden kollar lokalt i cachen om anv�ndaren finns sparad i cachen
	 * och f�rs�ker i s�dana fall verifiera inmatade uppgifter emot dessa.
	 * @param username
	 *            Anv�ndarnamnet
	 * @param password
	 *            L�senordet
	 * @return true om verifieringen g�r bra, false annars
	 */
	private LoginResponse evaluateLocally(String userName, String password) {
		/*
		 * H�mta anv�ndarens salt s� att encrypt() kan hasha korrekt String salt
		 * = ClientDatabaseManager.getSalt(username); password =
		 * Encryption.encrypt(password, salt);
		 */
		try{
			ArrayList<String> cachedUser = DatabaseController.db.getCachedUserRow(userName);
			String salt = cachedUser.get(2);
			password = Encryption.encrypt(password, salt);
			if (password.equals(cachedUser.get(1))){
				/*
				 * StubbornLoginThread f�rs�ker logga in mot servern med j�mna
				 * mellanrum
				 */
				SessionController.appIsRunning = true;
				s = new StubbornLoginThread(userName, password);
				Log.d("LoginManager.java", "S har nu skapats i evaluateLocally()");
				return LoginResponse.ACCEPTED_NO_CONNECTION;
			}
		}catch(Exception e){
			Log.e("SQLException", e.toString());
			return LoginResponse.NO_CONNECTION;

		}
		return LoginResponse.NO_CONNECTION;
	}

	public static void cache(String userName, String password, String salt) {
		DatabaseController.db.chacheUser(userName, password, salt);
	}

	public static void removeCache(String userName) {
		DatabaseController.db.decacheUser(userName);
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
			thread_is_active = true;
			nLoginThreads++;
			Log.d("LoginManager.java", "Number of active LoginThreads: " + nLoginThreads );
			this.username = username;
			this.password = password;
			thrd.start();
		}

		public void run() {
			while (SessionController.appIsRunning) {
				try {
					Thread.sleep(30000);
					if (server_connection_achieved) {
						Log.d("LoginManager.java", "Avbryter StubbornLoginThread... ");
						nLoginThreads--;
						thread_is_active = false;
						return;
					}
					Log.d("LoginManager.java", "About to evaluate in StubbornLoginThread... ");
					evaluate(username, password,false);
				} catch (InterruptedException e) {
					Log.d("LoginManager.java", "evaluateLocally FAILADE!!");
					continue;
				}
			}
		}

	}
}
