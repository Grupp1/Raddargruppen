package raddar.models;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class ClientDatabase extends SQLiteOpenHelper{
	private static final int DATABASE_VERSION = 2;
	private static final String DICTIONARY_TABLE_NAME = "Inbox";
	//DEFINIERA HUR DE SKA SE UT
	private static final String DICTIONARY_TABLE_CREATE =
			"CREATE TABLE " + DICTIONARY_TABLE_NAME + " (" +
					"KEY_WORD" + " TEXT, " +
					"KEY_DEFINITION" + " TEXT);";
	
	public ClientDatabase(Context context) {
		super(context, DICTIONARY_TABLE_NAME, null, DATABASE_VERSION);
		
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	
}
