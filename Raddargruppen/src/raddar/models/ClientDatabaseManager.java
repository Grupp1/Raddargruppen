package raddar.models;

import java.util.ArrayList;
import java.util.Observable;

import com.google.android.maps.GeoPoint;
import com.google.gson.Gson;

import raddar.enums.MessageType;
import raddar.enums.SituationPriority;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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
	private final String[] TEXT_MESSAGE_TABLE_ROWS = new String[] { "msgId",
			"srcUser", "rDate", "subject", "mData" };
	private final String[] CONTACT_TABLE_ROWS = new String[] { "userName",
			"isGroup", "sipUsr", "sipPw" };
	private final String[] SITUATION_TABLE_ROWS = new String[] { "title",
			"description", "priority" };
	private final String[] MAP_TABLE_ROWS = new String[] { "mapObject",
			"class", "id" };
	private final String[] SIP_DETAILS = new String[] { "userName", "password" };
	private final String[]	TABEL_VERSION = new String[] { "message"};

	/**********************************************************************
	 * CREATE OR OPEN A DATABASE SPECIFIC TO THE USER
	 * @param context 
	 * @param userName The name of the database is equal to the user name
	 */
	public ClientDatabaseManager(Context context, String userName) {
		this.context = context;
		this.DB_NAME = userName;
		CustomSQLiteOpenHelper helper = new CustomSQLiteOpenHelper(context);
		this.db = helper.getWritableDatabase();
		// TEST KOD ANVÄNDS FÖR ATT TESTA KONTAKTLISTAN
		/*
		 * addRow(new Contact("Alice",false)); addRow(new
		 * Contact("Borche",false)); addRow(new Contact("Daniel",false));
		 */

		// TEST KOD FÖR MAP
		addRow(new Fire(new GeoPoint(58395730, 15573080), "Här brinner det!",
				SituationPriority.HIGH));

		//TEST KOD FÖR SAMTAL
		//addSipProfile( user, String password);
		Contact einar = new Contact("Einar", false, "marcuseinar", "einar");
		Contact danan = new Contact("danan612", false, "danan612", "raddar");
		Contact alice = new Contact("Alice",false,null,null);
		Contact borche = new Contact("Borche", false);
		addRow(alice);
		addRow(einar);
		addRow(danan);
		addRow(borche);
	}

	/**********************************************************************
	 * ADDING A MESSAGE ROW TO THE DATABASE TABLE
	 * @param m The message that is to be added to the database
	 */
	public void addRow(Message m,boolean notify) {
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
		if(!notify){
		setChanged();
		notifyObservers(m);
		}
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
		setChanged();
		notifyObservers(c);
	}

	/**********************************************************************
	 * ADDING A SITUATION IN THA DATABASE TABLE
	 * 
	 * @param mo the MapObject that is to be added to the database
	 */
	public void addRow(MapObject mo) {
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
		// setChanged();
		// notifyObservers(mo);
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
		setChanged();
		notifyObservers(r);
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

	public void deleteRow(MapObject mo) {
		try {
			db.delete("map", "id = '" + mo.getId() + "'", null);
		} catch (Exception e) {
			Log.e("DB ERROR", e.toString());
			e.printStackTrace();
		}
		setChanged();
		notifyObservers(mo);
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
	 * ADDS A SIP PROFILE TO A CONTACT
	 * @param user Sip user name
	 * @param password Sip password
	 */
	public void addSipProfile(String user,String password){
		ContentValues values = new ContentValues();
		values.put("userName", user);
		values.put("password", password);
		try {
			db.insert("sip", null, values);
		} catch (Exception e) {
			Log.e("DB ERROR", e.toString());
			e.printStackTrace();
		}
	}
	/**********************************************************************
	 * DENNA METOD SKA INTE AVRA KVAR I SLUTÄNDAN
	 * @return
	 */
	public String[] getSipProfile() {
		String[] temp = new String[3];
		Cursor cursor = null;
		try {
			cursor = db.query("sip", SIP_DETAILS, null, null, null, null, null);
			cursor.moveToFirst();
			temp[0] = cursor.getString(0);
			temp[1] = cursor.getString(1);
			temp[2] = "ekiga.net";

		} catch (SQLException e) {
			Log.e("DB Error", e.toString());
			e.printStackTrace();
		}
		cursor.close();
		return temp;
	}
	/**
	 * Closes the database
	 */
	public void close(){
		db.close();
	}

	/**
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
			if (table.equals("message")) {
				cursor = db.query("message", TEXT_MESSAGE_TABLE_ROWS, null,
						null, null, null, null);
			} else if (table.equals("contact")) {
				cursor = db.query("contact", CONTACT_TABLE_ROWS, null, null,
						null, null, null);
			} else if (table.equals("map")) {
				cursor = db.query("map", MAP_TABLE_ROWS, null, null, null,
						null, null);
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
					} else if (table.equals("contact")) {
						Contact c = new Contact(cursor.getString(0), false,cursor.getString(2),cursor.getString(3));
						dataArrays.add(c);
					} else if (table.equals("map")) {
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
	

	public void updateVersion(int version,String rowName){
		ContentValues values = new ContentValues();
		values.put(rowName, version);
		try {
			db.update("version", values,
					null, null);
		} catch (Exception e) {
			Log.e("DB Error", e.toString());
			e.printStackTrace();
		}
	}
	public int getVersionNumber(String rowName) {
		int versionNumber = 0;
		Cursor cursor = null;
		try {
			cursor = db.query("version", SIP_DETAILS, null, null, null, null, null);
			cursor.moveToFirst();
			versionNumber = cursor.getInt(0);

		} catch (SQLException e) {
			Log.e("DB Error", e.toString());
			e.printStackTrace();
		}
		cursor.close();
		return versionNumber;
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
			String messageTableQueryString = "create table message ("
					+ "msgId integer primary key autoincrement not null,"
					+ "srcUser text, " + "rDate integer, " + "subject text, "
					+ "mData text)";
			String contactTableQueryString = "create table contact ("
					+ "userName text, " + "isGroup text, " + "sipUsr text, "
					+ "sipPw text)";
			String mapTableQueryString = "create table map ("
					+ "mapObject text," + "class text," + "id text)";
			String sipTableQueryString = "create table sip ("
					+ "userName text," + "password text)";
			String tableVersion = "create table version ("
					+ "mapId integer not null)";
			/*
			 * String newTableQueryString = "create table " + TABLE_NAME + " ("
			 * + TABLE_ROW_ID + " integer primary key autoincrement not null," +
			 * TABLE_ROW_ONE + " text," + TABLE_ROW_TWO + " text" + ");";
			 */
			// execute the query string to the database.
			db.execSQL(tableVersion);
			db.execSQL(sipTableQueryString);
			db.execSQL(mapTableQueryString);
			db.execSQL(contactTableQueryString);
			db.execSQL(messageTableQueryString);
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