package raddar.controllers;

import java.util.ArrayList;

/**
 * Controller for a user log in session
 * @author danan612
 *
 */
public class SessionController {
	
	private static ArrayList<String> onlineUsers = new ArrayList<String>();

	private static String user;
	/**
	 * Create new session on the client
	 * @param user The user whom is the owner of the session
	 */
	public SessionController(String user){
		this.user = user;

	}
	/**
	 * Get the current user
	 * @return The user whom is logged in
	 */
	public static String getUser() {
		return user;
	}
	
	public void setOnlineUsers(ArrayList<String> onlineUsers){
		this.onlineUsers = onlineUsers;
	}
	
	public ArrayList getOnlineUsers(){
		return onlineUsers;
	}
	
	public static void addOnlineUser(String userName){
		if(!onlineUsers.contains(userName));
			onlineUsers.add(userName);
	}
	
	public static void removeOnlineUser(String userName){
		onlineUsers.remove(userName);
	}

}
