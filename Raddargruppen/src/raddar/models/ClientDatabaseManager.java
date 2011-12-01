package raddar.models;

import java.util.ArrayList;
import java.util.Observable;

import raddar.enums.MessageType;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.gson.Gson;

public class ClientDatabaseManager extends Observable {
	// the Activity or Application that is creating an object from this class.
	Context context;

	// a reference to the database used by this application/object
	private SQLiteDatabase db;

	// Databse constants
	private String DB_NAME; // Same as username
	private final int DB_VERSION = 1;
	private static final String DB_PATH = "/data/data/YOUR_PACKAGE/databases/";

	// Table row constants

	private final String[] TEXT_MESSAGE_TABLE_ROWS = new String[] { "msgId", "srcUser", "rDate", "subject", "mData" };
	private final String[] CONTACT_TABLE_ROWS = new String[] { "userName","isGroup", "sipUsr", "sipPw" };
	private final String[] MAP_TABLE_ROWS = new String[] { "mapObject","class", "id" };
	private final String[] OUTBOX_TABLE_ROWS = new String[] { "msgID", "destUser", "rDate", "subject", "mData"};
	private final String[] DRAFT_TABLE_ROWS = new String[] { "msgID", "destUser", "rDate", "subject", "mData"};
	private final String[] IMAGE_MESSAGE_TABLE_ROWS = new String [] {"msgId", "srcUser", "rDate", "subject", "filePath"};


	/**********************************************************************
	 * CREATE OR OPEN A DATABASE SPECIFIC TO THE USER
	 * @param context 
	 * @param userName The name of the database is equal to the user name
	 */

	public ClientDatabaseManager(Context context) {
		this.context = context;
		this.DB_NAME = "client_database";
		CustomSQLiteOpenHelper helper = new CustomSQLiteOpenHelper(context);
		this.db = helper.getWritableDatabase();

		clearDatabase();
	}
		// TEST KOD ANVÄNDS FÖR ATT TESTA KONTAKTLISTAN
		/*
		 * addRow(new Contact("Alice",false)); addRow(new
		 * Contact("Borche",false)); addRow(new Contact("Daniel",false));
		 */

		// TEST KOD FÖR MAP
		//addRow(new Fire(new GeoPoint(58395730, 15573080), "Här brinner det!", SituationPriority.HIGH));

		//TEST KOD FÖR SAMTAL
		//addSipProfile( user, String password);
		Contact einar = new Contact("Einar", false, "marcuseinar", "einar");
		Contact danan = new Contact("danan612", false, "danan612", "raddar");
		Contact lalle = new Contact("lalle", false, "lalle", "lalle");
		Contact alice = new Contact("Alice",false,null,null);
		Contact borche = new Contact("Borche", false, "borche", "hej123");
		Contact mange = new Contact("Mange", false, "magkj501", "magkj501");
		
//		addRow(einar);
//		addRow(danan);
//		addRow(lalle);
//		addRow(borche);
//		addRow(mange);

	
	/**********************************************************************
	 * ADDING A MESSAGE ROW TO THE DATABASE TABLE
	 * @param m The message that is to be added to the database
	 */
	public void addRow(Message m, boolean notify) {
		ContentValues values = new ContentValues();
		values.put("srcUser", m.getSrcUser());
		values.put("rDate", m.getDate());
		values.put("subject", m.getSubject());
		values.put("mData", m.getData());
		try {
			db.insert("message", null, values);
		} catch (Exception e) {
			Log.e("DB ERROR", e.toString());
			e.printStackTrace();
		}
		if(notify) {
			setChanged();
			notifyObservers(m);
		}
	}

	/**********************************************************************
	 * 
	 * Messages to be addad to the outbox
	 * @param m The message that is to be added to the database
	 */
	public void addOutboxRow(Message m){
		ContentValues values = new ContentValues();
		values.put("destUser", m.getDestUser());
		Log.e("destUser", m.getDestUser().toString());
		values.put("rDate", m.getDate());
		values.put("subject", m.getSubject());
		values.put("mData", m.getData());
		try {
			db.insert("outbox", null, values);
		} catch (Exception e) {
			Log.e("DB ERROR", e.toString());
			e.printStackTrace();
		}
		setChanged();
		notifyObservers(m);			
	}

	/**********************************************************************
	 * ADDING A IMAGEMESSAGE ROW TO THE DATABASETABLE
	 *
	 * Messages to be added to the imageMessage database
	 * @param m The message that is to be added to the database
	 */
	public void addImageMessageRow(ImageMessage m){
		ContentValues values = new ContentValues();
		values.put("destUser", m.getDestUser());
		values.put("rDate", m.getDate());
		values.put("subject", m.getSubject());
		values.put("filePath", m.getFilePath());
		try {
			db.insert("imageMessage", null, values);
		} catch (Exception e) {
			Log.e("DB ERROR", e.toString());
			e.printStackTrace();
		}
		setChanged();
		notifyObservers(m);			
	}

	/**********************************************************************
	 * ADDING A CONTACT ROW IN THE DATABASE TABLE
	 *
	 * Messages to be addad to drafts
	 * @param m The message that is to be added to the database
	 */
	public void addDraftRow(Message m){
		ContentValues values = new ContentValues();
		values.put("destUser", m.getDestUser());
		values.put("rDate", m.getDate());
		values.put("subject", m.getSubject());
		values.put("mData", m.getData());
		try {
			db.insert("drafts", null, values);
		} catch (Exception e) {
			Log.e("DB ERROR", e.toString());
			e.printStackTrace();
		}
		setChanged();
		notifyObservers(m);			
	}

	/**********************************************************************
	 * ADDING A CONTACT ROW IN THE DATABASE TABLE
	 * 
	 * @param c The contact that is to be added to the database
	 */
	public void addRow(Contact c) {
		ContentValues values = new ContentValues();
		values.put("userName", c.getUserName());
		values.put("isGroup", "0");
		values.put("sipusr", c.getSipUsr());
		values.put("sipPw", c.getSipPw());
		try {
			db.insert("contact", null, values);
		} catch (Exception e) {
			Log.e("DB ERROR", e.toString());
			e.printStackTrace();
		}
//		setChanged();
//		notifyObservers(c);
	}

	/**********************************************************************
	 * ADDING A SITUATION IN THA DATABASE TABLE
	 * 
	 * @param mo the MapObject that is to be added to the database
	 */
	public void addRow(MapObject mo,boolean notify) {
		ContentValues values = new ContentValues();
		Gson gson = new Gson();
		values.put("mapObject", gson.toJson(mo));
		values.put("class", mo.getClass().getName());
		values.put("id", mo.getId());
		try {
			db.insert("map", null, values);
		} catch (Exception e) {
			Log.e("DB ERROR", e.toString());
			e.printStackTrace();
		}
		setChanged();
		if(notify){
			notifyObservers(mo);
		}	

	}

	/**********************************************************************
	 * ADDING A RESOURCE IN THA DATABASE TABLE
	 * 
	 * @param r The Resource that is to be added to the database
	 */
	public void addRow(Resource r) {
		ContentValues values = new ContentValues();
		values.put("title", r.getTitle());
		values.put("status", r.getStatus().toString());
		try {
			db.insert("contact", null, values);
		} catch (Exception e) {
			Log.e("DB ERROR", e.toString());
			e.printStackTrace();
		}
	}

	/**********************************************************************
	 * DELETING A ROW FROM THE CONTACT TABLE
	 * 
	 * @param c The contact that is to be deleted
	 */
	public void deleteRow(Contact c) {
		try {
			db.delete("contact", "userName = '" + c.getUserName() + "'", null);
		} catch (Exception e) {
			Log.e("DB ERROR", e.toString());
			e.printStackTrace();
		}
		setChanged();
		notifyObservers(c);
	}

	/**********************************************************************
	 * UPDATING A ROW IN THE CONTACT TABLE
	 * 
	 * @param c the Contact that is to be updated (with its old name)
	 * @param userName The new user name for contact c
	 */
	public void updateRow(Contact c, String userName) {
		ContentValues values = new ContentValues();
		values.put("userName", userName);
		values.put("sipusr", c.getSipUsr());
		values.put("sipPw", c.getSipPw());
		try {
			db.update("contact", values,
					"userName = '" + c.getUserName() + "'", null);
		} catch (Exception e) {
			Log.e("DB Error", e.toString());
			e.printStackTrace();
		}
	}
	
	/**********************************************************************
	 * UPDATING A ROW IN THE MAPOBJECT TABLE
	 * 
	 * @param c 
	 * @param 
	 */
	public void updateRow(MapObject o) {
		ContentValues values = new ContentValues();
		values.put("mapObject", new Gson().toJson(o));
		values.put("class", o.getClass().getName());
		try {
			db.update("map", values,
					"id = '" + o.getId() + "'", null);
		} catch (Exception e) {
			Log.e("DB Error", e.toString());
			e.printStackTrace();
		}
	}
	
	/**
	 * Closes the database
	 */
	public void close(){
		db.close();
	}
	public void clearDatabase(){
		db.delete("message", null, null);
		db.delete("map", null, null);
		db.delete("contact", null, null);
		
	}

	/********************************************************************
	 * DELETING A ROW IN THE DRAFT TABLE
	 * 
	 * @param 
	 * @return
	 */

	public void deleteDraftRow(Message m) {
		try {
			db.delete("drafts", "destUser = '" + m.getDestUser().toString().trim() +"'", null);
		} catch (Exception e) {
			Log.e("DB ERROR", e.toString());
			e.printStackTrace();
		}
		//		setChanged();
		//		notifyObservers(m);
	}

	/********************************************************************
	 * 
	 * @param 
	 * @return
	 */

	public void deleteRow(MapObject mo) {
		try {
			db.delete("map", "id = '" +mo.getId()+"'", null);
		} catch (Exception e) {
			Log.e("DB ERROR", e.toString());
			e.printStackTrace();
		}
		setChanged();
		notifyObservers(mo);
	}


	/********************************************************************
	 * RETRIEVE ALL ROWS IN A TABLE AS AN ArrayList
	 * 
	 * @param table The table that is to be retrieved
	 * @return an ArrayList that contains all rows in a table. Each row in itself is an ArrayList that contains each collumn of a row.
	 */
	public ArrayList getAllRowsAsArrays(String table) {
		// TODO gör så denna funktion fungerar med alla databastabeller
		// TODO kom inte på något bra sätt för att få det att fungera i det
		// generella fallet.
		ArrayList dataArrays = new ArrayList();
		Cursor cursor = null;
		try {
			// ask the database object to create the cursor.
			if(table.equals("message")){
				cursor = db.query(
						"message",
						TEXT_MESSAGE_TABLE_ROWS,
						null, null, null, null, null);
			}
			else if(table.equals("imageMessage")){
				cursor = db.query(
						"imageMessage",
						IMAGE_MESSAGE_TABLE_ROWS,
						null, null, null, null, null);
			}
			else if (table.equals("outbox")){
				cursor = db.query("outbox",	OUTBOX_TABLE_ROWS,
						null, null , null, null, null);
			}
			else if (table.equals("drafts")){
				cursor = db.query("drafts",	DRAFT_TABLE_ROWS,
						null, null , null, null, null);
			}
			else if(table.equals("contact")){
				cursor = db.query(
						"contact", CONTACT_TABLE_ROWS,
						null, null, null, null, null);
			}
			else if(table.equals("map")){
				cursor = db.query(
						"map",
						MAP_TABLE_ROWS,
						null, null, null, null, null);
			}
			cursor.moveToFirst();
			// If it is a message table
			if (!cursor.isAfterLast()) {
				do {
					if (table.equals("message")) {
						Message m = new TextMessage(MessageType.TEXT,
								cursor.getString(1), DB_NAME);
						m.setSubject(cursor.getString(3));
						m.setData(cursor.getString(4));
						dataArrays.add(m);
					} 
					else if (table.equals("imageMessage")) {
						Message m = new ImageMessage(MessageType.IMAGE,
								cursor.getString(1), DB_NAME,
								 cursor.getString(4));
						m.setSubject(cursor.getString(3));
						dataArrays.add(m);
					}
					else if (table.equals("contact")) {
						Contact c = new Contact(cursor.getString(0), false,cursor.getString(2),cursor.getString(3));
						dataArrays.add(c);
					}
					else if(table.equals("outbox")){
						Message m = new TextMessage(MessageType.TEXT, 
								cursor.getString(1), 
								DB_NAME,  
								cursor.getString(4));
						m.setSubject(cursor.getString(3));
						dataArrays.add(m);
					}
					else if(table.equals("drafts")){
						Message m = new TextMessage(MessageType.TEXT, 
								cursor.getString(1), 
								DB_NAME,  
								cursor.getString(4));
						m.setSubject(cursor.getString(3));
						dataArrays.add(m);
					}
					else if (table.equals("map")) {
						Gson gson = new Gson();
						Class c = null;
						try {
							c = Class.forName(cursor.getString(1));
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						}
						MapObject mo = gson.fromJson(cursor.getString(0), c);
						dataArrays.add(mo);
					}
				}
				// move the cursor's pointer up one position.
				while (cursor.moveToNext());
			}
		} catch (SQLException e) {
			Log.e("DB Error", e.toString());
			e.printStackTrace();
		}
		// return the ArrayList that holds the data collected from
		// the database.
		cursor.close();
		return dataArrays;
	}


	private void openDatabaseReadOnly() throws SQLiteException{
		String myPath = DB_PATH + DB_NAME;
		db = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
	}

	private void openDatabaseReadWrite() throws SQLiteException{
		String myPath = DB_PATH + DB_NAME;
		db = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
	}
	/**
	 * CREATES A CostumSQLiteHelper THAT MANAGES DATABASE CREATION IN SQLITE
	 */
	private class CustomSQLiteOpenHelper extends SQLiteOpenHelper {
		public CustomSQLiteOpenHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// This string is used to create the database. It should
			// be changed to suit your needs.

			String contactTableQueryString = "create table contact ("
					+ "userName text, " + "isGroup text, " + "sipUsr text, "
					+ "sipPw text)";

			String sipTableQueryString = "create table sip ("
					+ "userName text," + "password text)";

			String messageTableQueryString = "create table message (" +
					"msgId integer primary key autoincrement not null," +
					"srcUser text, " +
					"rDate integer, " +
					"subject text, " +
					"mData text)";

			String imageMessageTableQueryString = "create table imageMessage (" +
					"msgId integer primary key autoincrement not null," +
					"srcUser text, " +
					"rDate integer, " +
					"subject text, " +
					"filePath text)";
			
			String mapTableQueryString = "create table map (" +
					"mapObject text," +
					"class text," +
					"id text)";

			String outboxTableQueryString = "create table outbox (" 
					+ "msgId integer primary key autoincrement not null," + 
					"destUser text," +
					"rDate integer," +
					"subject text," +
					"mData text)";

			String draftTableQueryString = "create table drafts (" 
					+ "msgId integer primary key autoincrement not null," + 
					"destUser text," +
					"rDate integer," +
					"subject text," +
					"mData text)";


			/*
			 * String newTableQueryString = "create table " + TABLE_NAME + " ("
			 * + TABLE_ROW_ID + " integer primary key autoincrement not null," +
			 * TABLE_ROW_ONE + " text," + TABLE_ROW_TWO + " text" + ");";
			 */
			// execute the query string to the database.
			db.execSQL(sipTableQueryString);
			db.execSQL(mapTableQueryString);
			db.execSQL(contactTableQueryString);
			db.execSQL(messageTableQueryString);
			db.execSQL(imageMessageTableQueryString);
			db.execSQL(outboxTableQueryString);
			db.execSQL(draftTableQueryString);
		}

		/**
		 * Used only if you want to update the SQLite database version. (Will not be used.)
		 */
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// NOTHING TO DO HERE. THIS IS THE ORIGINAL DATABASE VERSION.
			// OTHERWISE, YOU WOULD SPECIFIY HOW TO UPGRADE THE DATABASE.
		}
	}

}