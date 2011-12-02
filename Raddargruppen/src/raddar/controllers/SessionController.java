package raddar.controllers;

import java.util.Observable;

import raddar.enums.ConnectionStatus;
import raddar.views.StartView;

/**
 * Controller for a user log in session
 * @author danan612
 *
 */
public class SessionController extends Observable{
	
	private static String user;
	private static ConnectionStatus connection = ConnectionStatus.DISCONNECTED;
	private static SessionController sessionController;
	private static String userName;
	 static String getUserName() {
		return userName;
	}
	public static void setUserName(String userName) {
		SessionController.userName = userName;
	}
	public static String getPassword() {
		return password;
	}
	public static void setPassword(String password) {
		SessionController.password = password;
	}

	public static String password;
	/**
	 * Create new session on the client
	 * @param user The user whom is the owner of the session
	 */
	public SessionController(String user){
		this.user = user;
		sessionController = this;
	}
	/**
	 * Get the current user
	 * @return The user whom is logged in
	 */
	public static String getUser() {
		return user;
	}
	public void changeConnectionStatus(ConnectionStatus status){
		this.connection = status;
		setChanged();
		notifyObservers(status);
	}
	
	public static SessionController getSessionController(){
		return sessionController;
	}
	public static ConnectionStatus getConnectionStatus(){
		return connection;
	}
}
