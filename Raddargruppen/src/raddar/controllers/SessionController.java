package raddar.controllers;

import raddar.enums.ConnectionStatus;
import raddar.gruppen.R;
import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.Window;

/**
 * Controller for a user log in session
 * @author danan612
 *
 */
public class SessionController {

	private static String user;
	private static ConnectionStatus connection = ConnectionStatus.DISCONNECTED;
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
 * Sätter utseendet på titleBar
 * @param a activity som den anropas från
 * @param s app_name + / var man är
 */
	public static void titleBar(Activity a, String s){
		a.setTitle("Räddargruppen" + s);
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
}
