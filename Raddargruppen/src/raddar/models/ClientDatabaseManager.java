package raddar.models;

import java.util.ArrayList;
import java.util.Observable;

import com.google.android.maps.GeoPoint;
import com.google.gson.Gson;

import raddar.enums.MessagePriority;
import raddar.enums.MessageType;
import raddar.enums.SituationPriority;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ClientDatabaseManager extends Observable {
	// the Activity or Application that is creating an object from this class.
	Context context;

	// a reference to the database used by this application/object
	private SQLiteDatabase db;

	// Databse constants
	private String DB_NAME; //Same as username
	private final int DB_VERSION = 1;

	// Table row constants
	private final String[] TEXT_MESSAGE_TABLE_ROWS = new String[] { "msgId",
			"srcUser","rDate","subject","mData"};
	private final String[] CONTACT_TABLE_ROWS = new String[] { "userName", "isGroup"};
	private final String[] SITUATION_TABLE_ROWS = new String[] { "title", "description", "priority" };
	private final String[] MAP_TABLE_ROWS = new String[] { "mapObject","class"};


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
		//TEST KOD ANVÄNDS FÖR ATT TESTA KONTAKTLISTAN
		addRow(new Contact("Alice",false));
		addRow(new Contact("Borche",false));
		addRow(new Contact("Daniel",false));
		
		//TEST KOD FÖR MAP
		addRow(new Fire(new GeoPoint(58395730, 15573080), "HAHAHA", "HAHHAHA", SituationPriority.HIGH));
	}

	/**********************************************************************
	 * ADDING A MESSAGE ROW TO THE DATABASE TABLE
	 * 
	 * @param m The message that is to be added to the database
	 */
	public void addRow(Message m) {
		ContentValues values = new ContentValues();
		values.put("srcUser", m.getSrcUser());
		values.put("rDate", m.getFormattedDate());
		values.put("subject", m.getSubject());
		values.put("mData", m.getData());
		try {
			db.insert("message", null, values);
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
	 * @param c The contact that is to be added
	 */
	public void addRow(Contact c){
		ContentValues values = new ContentValues();
		values.put("userName", c.getUserName());
		values.put("isGroup", "0");
		try {
			db.insert("contact", null, values);
		} catch (Exception e) {
			Log.e("DB ERROR", e.toString());
			e.printStackTrace();
		}
		//setChanged();
		//notifyObservers(c);
	}

	/**********************************************************************
	 * ADDING A SITUATION IN THA DATABASE TABLE
	 * 
	 * @param s The situation that is to be added
	 */
	public void addRow(MapObject mo){
		ContentValues values = new ContentValues();
		Gson gson = new Gson();
		values.put("mapObject", gson.toJson(mo));
		values.put("class", mo.getClass().getName());
		try {
			db.insert("map", null, values);
		} catch (Exception e) {
			Log.e("DB ERROR", e.toString());
			e.printStackTrace();
		}
	//	setChanged();
	//	notifyObservers(mo);
	}

	/**********************************************************************
	 *ADDING A RESOURCE IN THA DATABASE TABLE
	 *
	 *
	 */
	public void addRow(Resource r){
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
	 *@param c The contact that is to be deleted
	 */
	public void deleteRow(Contact c) {
		try {
			db.delete("contact", "userName =" + c.getUserName(), null);
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
	 */
	public void updateRow(Contact c, String userName) {
		ContentValues values = new ContentValues();
		values.put("srcUser", userName);
		try {
			db.update("contact", values, "userName" + c.getUserName(), null);
		} catch (Exception e) {
			Log.e("DB Error", e.toString());
			e.printStackTrace();
		}
	}

	/**********************************************************************
	 * RETRIEVING A ROW FROM THE DATABASE TABLE
	 * 
	 * This is an example of how to retrieve a row from a database table using
	 * this class. You should edit this method to suit your needs.
	 * 
	 * @param rowID
	 *            the id of the row to retrieve
	 * @return an array containing the data from the row
	 */
	/*	public ArrayList<Message> getRowAsArray() {
		// create an array list to store data from the database row.
		// I would recommend creating a JavaBean compliant object
		// to store this data instead. That way you can ensure
		// data types are correct.
		ArrayList<Message> rowArray = new ArrayList<Message>();
		Cursor cursor;

		try {
			// this is a database call that creates a "cursor" object.
			// the cursor object store the information collected from the
			// database and is used to iterate through the data.
			cursor = db.query("message", new String[] { "msgId",
					"srcUser","rDate","subject","mData"}, null,
					null, null, null, null, null);

			// move the pointer to position zero in the cursor.
			cursor.moveToFirst();

			// if there is data available after the cursor's pointer, add
			// it to the ArrayList that will be returned by the method.
			if (!cursor.isAfterLast()) {
				do {
					Message m = new TextMessage(MessageType.TEXT, 
							cursor.getString(1), 
							DB_NAME, 
							MessagePriority.NORMAL, 
							cursor.getString(4));
					rowArray.add(m);
				} while (cursor.moveToNext());
			}

			// let java know that you are through with the cursor.
			cursor.close();
		} catch (SQLException e) {
			Log.e("DB ERROR", e.toString());
			e.printStackTrace();
		}

		// return the ArrayList containing the given row from the database.
		return rowArray;
	} */


	/**
	 * RETRIEVE ALL ROWS IN A TABLE AS AN ArrayList
	 * @param table The table that is to be retrieved
	 * @return
	 */
	public ArrayList getAllRowsAsArrays(String table){
		//TODO gör så denna funktion fungerar med alla databastabeller
		//TODO kom inte på något bra sätt för att få det att fungera i det generella fallet.
		ArrayList dataArrays = new ArrayList();
		Cursor cursor = null;
		try
		{
			// ask the database object to create the cursor.
			if(table.equals("message")){
				cursor = db.query(
						"message",
						TEXT_MESSAGE_TABLE_ROWS,
						null, null, null, null, null);
			}
			else if(table.equals("contact")){
				cursor = db.query(
						"contact",
						CONTACT_TABLE_ROWS,
						null, null, null, null, null);
			}
			else if(table.equals("map")){
				cursor = db.query(
						"map",
						MAP_TABLE_ROWS,
						null, null, null, null, null);
			}
			cursor.moveToFirst();
			//If it is a message table
			if (!cursor.isAfterLast())
			{
				do
				{
					if(table.equals("message")){
						Message m = new TextMessage(MessageType.TEXT, 
								cursor.getString(1), 
								DB_NAME, 
								MessagePriority.NORMAL, 
								cursor.getString(4));
						m.setSubject(cursor.getString(3));
						dataArrays.add(m);
					}
					else if(table.equals("contact")){
						Contact c = new Contact(cursor.getString(0),false);
						dataArrays.add(c);
					}
					else if(table.equals("map")){
						Gson gson = new Gson();
						Class c = null;
						try {
							c = Class.forName(cursor.getString(1));
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						}					
						MapObject mo = gson.fromJson(cursor.getString(0), c);
						dataArrays.add(c);
					}
				}
				// move the cursor's pointer up one position.
				while (cursor.moveToNext());
			}
		}
		catch (SQLException e)
		{
			Log.e("DB Error", e.toString());
			e.printStackTrace();
		}
		// return the ArrayList that holds the data collected from
		// the database.
		return dataArrays;
	}

	private class CustomSQLiteOpenHelper extends SQLiteOpenHelper {
		public CustomSQLiteOpenHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// This string is used to create the database. It should
			// be changed to suit your needs.
			String messageTableQueryString = "create table message (" +
					"msgId integer primary key autoincrement not null," +
					"srcUser text, " +
					"rDate integer, " +
					"subject text, " +
					"mData text)";
			String contactTableQueryString = "create table contact (" +
					"userName text, " +
					"isGroup text)";
			String mapTableQueryString = "create table map (" +
					"mapObject text," +
					"class text)";
			/*
			 * String newTableQueryString = "create table " + TABLE_NAME + " ("
			 * + TABLE_ROW_ID + " integer primary key autoincrement not null," +
			 * TABLE_ROW_ONE + " text," + TABLE_ROW_TWO + " text" + ");";
			 */
			// execute the query string to the database.
			db.execSQL(mapTableQueryString);
			db.execSQL(contactTableQueryString);
			db.execSQL(messageTableQueryString);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// NOTHING TO DO HERE. THIS IS THE ORIGINAL DATABASE VERSION.
			// OTHERWISE, YOU WOULD SPECIFIY HOW TO UPGRADE THE DATABASE.
		}
	}
}