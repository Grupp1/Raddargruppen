package raddar.models;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class ClientDatabase extends SQLiteOpenHelper implements Runnable{
	private static final int DATABASE_VERSION = 1;
	private static final String DICTIONARY_TABLE_NAME = "Inbox";
	//Skapa databasen "inbox" statement
	private static final String INBOX_CREATE = "create table inbox (from VARCHAR(20), to " +
			"VARCHAR(20), recieved-at DATETIME, subject VARCHAR(20), prioroty VARCHAR(5)";
	
	
	
	
	
	
	
	/*	//DEFINIERA HUR DE SKA SE UT
	private static final String DICTIONARY_TABLE_CREATE =
			"CREATE TABLE " + DICTIONARY_TABLE_NAME + " (" +
					"KEY_WORD" + " TEXT, " +
					"KEY_DEFINITION" + " TEXT);";
*/
	
	
	public ClientDatabase(Context context) {
		super(context, DICTIONARY_TABLE_NAME, null, DATABASE_VERSION);
		
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(INBOX_CREATE);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

	public void run() {
		// TODO Auto-generated method stub
		
	}
	
}
