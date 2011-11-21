package raddar.controllers;

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

}
