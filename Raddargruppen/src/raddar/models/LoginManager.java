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
	public void evaluate(String userName, String password, boolean firstLogIn) {
		try {
			
			//nya ssl
			SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
			SSLSocket sslsocket = (SSLSocket) sslsocketfactory.createSocket();
			Log.d("ssl",sslsocket.getSupportedCipherSuites()[0]);
			
			InetAddress addr = InetAddress.getByName(ServerInfo.SERVER_IP);
			int port = ServerInfo.SERVER_PORT;
			SocketAddress sockAddr = new InetSocketAddress(addr, port);
			int TIME_OUT = 5000;
			sslsocket.connect(sockAddr, TIME_OUT);

			sslsocket.setEnabledCipherSuites(new String[] { "SSL_DH_anon_WITH_RC4_128_MD5" });
			// Initiera handskakningen
			SSLSession session = sslsocket.getSession();
			
			PrintWriter pw = new PrintWriter(sslsocket.getOutputStream(), true);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					sslsocket.getInputStream()));

			RequestMessage rm = new RequestMessage(RequestType.SALT);
			rm.setSrcUser(userName);
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
				s = null;
				SessionController.setPassword(password);
				SessionController.setUserName(userName);
				if(SessionController.getSessionController()!=null)
					SessionController.getSessionController().changeConnectionStatus(ConnectionStatus.CONNECTED);
				logIn = LoginResponse.ACCEPTED;
				cache(userName, password, salt);
				sendBufferedMessages();
			}
			else if(response.equals("OK_FORCE_LOGOUT")){
				s = null;
				SessionController.setPassword(password);
				SessionController.setUserName(userName);
				if(SessionController.getSessionController()!=null)
					SessionController.getSessionController().changeConnectionStatus(ConnectionStatus.CONNECTED);
				logIn = LoginResponse.USER_ALREADY_LOGGED_IN;
				sendBufferedMessages();
			}else{
				s = null;
			}
		} catch (IOException e) {
			Log.d("NotificationMessage", "Server connection failed");
			// Om servern inte kan n�s, kolla om vi har en f�rs�kande tr�d redan
			// ...har vi en f�rs�kande tr�d inneb�r det att vi redan �r inloggade lokalt
			// och d� returnerar vi h�r, annars loggar vi in lokalt
			if(!firstLogIn && s==null){
				s = new StubbornLoginThread(userName, password);
				Log.d("LoginManager", "LULZ 3");
				return;
			}
			else if (s == null)
				logIn = evaluateLocally(userName, password);
			else
				return;
		}
		if(debugMode){
			logIn = LoginResponse.ACCEPTED_NO_CONNECTION;
		}
		setChanged();
		notifyObservers(logIn);
	}
	public boolean isRunningStubornLoginThread(){
		if(s == null)
			return false;
		return true;
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
				s = new StubbornLoginThread(userName, password);
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
			this.username = username;
			this.password = password;
			thrd.start();
		}

		public void run() {
			while (SessionController.appIsRunning) {
				try {
					if (s == null)
						break;
					evaluate(username, password,false);
					if (s == null)
						break;
					// V�nta tv� minuter mellan varje f�rs�k
					Thread.sleep(30000);

				} catch (InterruptedException e) {
					Log.d("LoginManager.java", "evaluateLocally FAILADE!!");
					continue;
				}
			}
		}

	}

}
