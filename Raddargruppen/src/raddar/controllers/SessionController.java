package raddar.controllers;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;

/**
 * Controller for a user log in session
 * @author danan612
 *
 */
public class SessionController {

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
/**
 * S�tter utseendet p� titleBar
 * @param a activity som den anropas fr�n
 * @param s app_name + / var man �r
 */
	public static void titleBar(Activity a, String s){
		a.setTitle("R�ddargruppen" + s);
		View title = a.getWindow().findViewById(android.R.id.title);
		View titleBar = (View) title.getParent();
		titleBar.setBackgroundColor(Color.rgb(48,128,20));
		
	}
}
