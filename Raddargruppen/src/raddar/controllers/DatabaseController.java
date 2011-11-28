package raddar.controllers;

import android.content.Context;
import raddar.models.ClientDatabaseManager;

/**
 * Has a static reference to the database
 * @author danan612
 *
 */
public class DatabaseController {
	/**
	 * SQLite database interface
	 */
	public static ClientDatabaseManager db;
	/**
	 * Start the connection to the database
	 * @param con The context which starts and owns the database
	 */
	public DatabaseController(Context con){
		db = new ClientDatabaseManager(con);
	}
}
