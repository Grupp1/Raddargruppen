package raddar.models;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Observable;

import raddar.enums.LoginResponse;
import raddar.enums.NotificationType;
import raddar.enums.ServerInfo;
import android.util.Log;

import com.google.gson.Gson;

public class LoginManager extends Observable {
	
	private static HashMap<String, String> passwordCache = new HashMap<String, String>();
	
	/**
	 * Verifierar att username och password är giltiga. Denna metoden kommer att
	 * försöker verifiera med servern. Om klienten inte får kontakt med servern så 
	 * kollar den lokalt i cachen om det finns någon entry sparad.
	 * 
	 * @param username Användarnamnet
	 * @param password Lösenordet
	 * @return true om verifieringen går bra, false annars
	 */
	public void evaluate(String username, String password) {
		LoginResponse logIn = LoginResponse.NO_SUCH_USER_OR_PASSWORD;
		NotificationMessage nm = new NotificationMessage(username, 
				NotificationType.CONNECT, 
				password);
		try {
			// Skapa socket som används för att skicka NotificationMessage
			Socket so = new Socket(InetAddress.getByName(ServerInfo.SERVER_IP), ServerInfo.SERVER_PORT);
			
			Log.d("Efter socketen", "lawl");
			String send = nm.getClass().getName()+"\r\n";
			send +=	new Gson().toJson(nm);
			PrintWriter pw = new PrintWriter(so.getOutputStream(), true);
			pw.println(send);
			
			BufferedReader br = new BufferedReader(
					new InputStreamReader(so.getInputStream()));
			
			// Läs in ett svar från servern via SAMMA socket
			String response = br.readLine();
			
			// Stäng ner strömmar och socket
			pw.close();
			br.close();
			so.close();
		
			// Om servern säger att inmatade uppgifter är giltiga, returnera true
			if (response.equals("OK")) 
				logIn = LoginResponse.ACCEPTED;
		} catch (IOException e) {
			Log.d("NotificationMessage", "Server connection failed");
			logIn = evaluateLocally(username, password);
		}
		
		// debug mode
		logIn = LoginResponse.ACCEPTED;
		
		setChanged();
		notifyObservers(logIn);
	}
		
	/**
	 * Denna metoden kollar lokalt i cachen om användaren finns sparad
	 * i cachen och försöker i sådana fall verifiera inmatade
	 * uppgifter emot dessa.
	 * 
	 * @param username Användarnamnet
	 * @param password Lösenordet
	 * @return true om verifieringen går bra, false annars
	 */
	private static LoginResponse evaluateLocally(String username, String password){
		String temp = passwordCache.get(username);
		if (temp == null) return LoginResponse.NO_CONNECTION;
		if (password.equals(temp)) return LoginResponse.ACCEPTED_NO_CONNECTION;
		return LoginResponse.NO_CONNECTION;
	}
	
	public static String cache(String username, String password) {
		return passwordCache.put(username, password);
	}
	
	public static String removeCache(String username) {
		return passwordCache.remove(username);
	}

}
