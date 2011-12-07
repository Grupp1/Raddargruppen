package raddar.controllers;


import java.util.ArrayList;
import java.util.Observable;

import raddar.enums.ConnectionStatus;
import raddar.gruppen.R;
import raddar.models.Contact;
import raddar.models.QoSManager;
import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.Window;


/**
 * Controller for a user log in session
 * @author danan612
 *
 */

public class SessionController extends Observable{
		
	private static ArrayList<String> onlineUsers = new ArrayList<String>();

	public static String password;
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

	public void changeConnectionStatus(final ConnectionStatus status){
		this.connection = status;
		setChanged();
		notifyObservers(status);
		QoSManager.getCurrentActivity().runOnUiThread(new Runnable(){
			public void run() {
				if (status.equals(ConnectionStatus.CONNECTED)){
					QoSManager.getCurrentActivity().setFeatureDrawableResource(Window.FEATURE_RIGHT_ICON, R.drawable.connected);
				
				}
				else if (status.equals(ConnectionStatus.DISCONNECTED)){
					QoSManager.getCurrentActivity().setFeatureDrawableResource(Window.FEATURE_RIGHT_ICON, R.drawable.disconnected);
				}
				
											
			}
		});
																															
	}
	
	public static void newToast(String data){
		sessionController.setChanged();
		sessionController.notifyObservers(data);
	}
	
	public static SessionController getSessionController(){
		return sessionController;
	}
	public static ConnectionStatus getConnectionStatus(){
		return connection;
	}
/**
 * S�tter utseendet p� titleBar
 * @param a activity som den anropas fr�n
 * @param s app_name + / var man �r
 */
	public static void titleBar(Activity a, String s){
		a.setTitle(" Alice" + s);
		View title = a.getWindow().findViewById(android.R.id.title);
		View titleBar = (View) title.getParent();
		if (connection.equals(ConnectionStatus.CONNECTED)){
			a.setFeatureDrawableResource(Window.FEATURE_RIGHT_ICON, R.drawable.connected);
		
		}
		else if (connection.equals(ConnectionStatus.DISCONNECTED)){
			a.setFeatureDrawableResource(Window.FEATURE_RIGHT_ICON, R.drawable.disconnected);
		}

		titleBar.setBackgroundColor(Color.rgb(48,128,20));
		
	}

	
	public void setOnlineUsers(ArrayList<String> onlineUsers){
		this.onlineUsers = onlineUsers;
	}
	
	public static ArrayList<Contact> getOnlineContacts(){
		ArrayList<Contact> temp = new ArrayList<Contact>();
		for(String s: onlineUsers){
			temp.add(new Contact(s,false));
		}
		return temp;
	}
	
	public static ArrayList getOnlineUsers(){
		return onlineUsers;
	}
	
	public void addOnlineUser(String userName){
		if(!onlineUsers.contains(userName));
			onlineUsers.add(userName);
		setChanged();
		notifyObservers(new Contact(userName,false));
	}
	
	public void removeOnlineUser(String userName){
		onlineUsers.remove(userName);
	}
	
	public static boolean isOnline(String userName){
		return onlineUsers.contains(userName);
	}


}
