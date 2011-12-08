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
import raddar.enums.MessageType;
import raddar.enums.NotificationType;
import raddar.enums.RequestType;
import raddar.enums.ServerInfo;
import android.database.SQLException;
import android.util.Log;

import com.google.gson.Gson;

public class LoginManager extends Observable {

	private static HashMap<String, String> passwordCache = new HashMap<String, String>();

	private StubbornLoginThread s = null;
	private LoginResponse logIn = LoginResponse.NO_SUCH_USER_OR_PASSWORD;
	/**
	 * Hï¿½rdkoda denna boolean true om klienten inte ska kontakta servern fï¿½r inloggning
	 */
	public boolean debugMode = false;

	/**
	 * Verifierar att username och password ï¿½r giltiga. Denna metoden kommer att
	 * fï¿½rsï¿½ker verifiera med servern. Om klienten inte fï¿½r kontakt med servern
	 * sï¿½ kollar den lokalt i cachen om det finns nï¿½gon entry sparad.
	 * 
<<<<<<< HEAD
	 * @param username
	 *            Anvï¿½ndarnamnet
=======
	 * @param userName
	 *            Användarnamnet
>>>>>>> cache
	 * @param password
	 *            Lï¿½senordet
	 * @return true om verifieringen gï¿½r bra, false annars
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

			// Lï¿½s in salt frï¿½n server
			String salt = br.readLine();

			// Anvï¿½nd saltet vi fick fï¿½r att salta och kryptera lï¿½senordet
			password = Encryption.encrypt(password, salt);

			// Skapa msg med användarnamn och krypterat lösenord
			NotificationMessage nm = new NotificationMessage(userName,
					NotificationType.CONNECT, password);
			send = nm.getClass().getName() + "\r\n";
			gg = new Gson().toJson(nm);
			send += gg;

			// Skicka det saltade och krypterade lï¿½senordet
			pw.println(send);

			// Lï¿½s in ett svar frï¿½n servern via SAMMA socket
			String response = br.readLine();

			// Stï¿½ng ner strï¿½mmar och socket
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
			// Om servern inte kan nï¿½s, kolla om vi har en fï¿½rsï¿½kande trï¿½d redan
			// ...har vi en fï¿½rsï¿½kande trï¿½d innebï¿½r det att vi redan ï¿½r inloggade lokalt
			// och dï¿½ returnerar vi hï¿½r, annars loggar vi in lokalt
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

	/**
	 * Denna metoden kollar lokalt i cachen om anvï¿½ndaren finns sparad i cachen
	 * och fï¿½rsï¿½ker i sï¿½dana fall verifiera inmatade uppgifter emot dessa.
	 * 
<<<<<<< HEAD
	 * @param username
	 *            Anvï¿½ndarnamnet
=======
	 * @param userName
	 *            Användarnamnet
>>>>>>> cache
	 * @param password
	 *            Lï¿½senordet
	 * @return true om verifieringen gï¿½r bra, false annars
	 */
	private LoginResponse evaluateLocally(String userName, String password) {
		/*
		 * Hï¿½mta anvï¿½ndarens salt sï¿½ att encrypt() kan hasha korrekt String salt
		 * = ClientDatabaseManager.getSalt(username); password =
		 * Encryption.encrypt(password, salt);
		 */
		try{
			ArrayList<String> cachedUser = DatabaseController.db.getCachedUserRow(userName);
			String salt = cachedUser.get(2);
			password = Encryption.encrypt(password, salt);
			if (password.equals(cachedUser.get(1))){
				/*
				 * StubbornLoginThread försöker logga in mot servern med jämna
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
		//Skicka alla medelanden som buffrats och tï¿½m sedan buffern
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
	 * Privat klass som fï¿½rsï¿½ker att logga in emot servern med jï¿½mna mellanrum
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
					evaluate(username, password,false);
					if (s == null)
						break;
					// Vï¿½nta tvï¿½ minuter mellan varje fï¿½rsï¿½k
					Thread.sleep(100);

				} catch (InterruptedException e) {
					Log.d("LoginManager.java", "evaluateLocally FAILADE!!");
					continue;
				}
			}
		}

	}

}
