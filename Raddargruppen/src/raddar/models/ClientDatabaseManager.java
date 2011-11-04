package raddar.models;

import java.util.ArrayList;
import java.util.Observable;

import raddar.enums.MessagePriority;
import raddar.enums.MessageType;

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

	// These constants are specific to the database. They should be
	// changed to suit your needs.
	private String DB_NAME;
	private final int DB_VERSION = 1;


	public ClientDatabaseManager(Context context, String userName) {
		this.context = context;
		this.DB_NAME = userName;

		// create or open the database
		CustomSQLiteOpenHelper helper = new CustomSQLiteOpenHelper(context);
		this.db = helper.getWritableDatabase();
	}

	/**********************************************************************
	 * ADDING A ROW TO THE DATABASE TABLE
	 * 
	 * This is an example of how to add a row to a database table using this
	 * class. You should edit this method to suit your needs.
	 * 
	 * the key is automatically assigned by the database
	 * 
	 * @param rowStringOne
	 *            the value for the row's first column
	 * @param rowStringTwo
	 *            the value for the row's second column
	 */
	public void addRow(String tableName, String[] tableCells) {
		// this is a key value pair holder used by android's SQLite functions
		ContentValues values = new ContentValues();
		values.put("srcUser", tableCells[0]);
		//	values.put("rDate", tableCells[1]);
		values.put("subject", tableCells[2]);
		values.put("mData", tableCells[3]);

		// ask the database object to insert the new data
		try {
			db.insert(tableName, null, values);
		} catch (Exception e) {
			Log.e("DB ERROR", e.toString());
			e.printStackTrace();
		}
		Message m = new TextMessage(MessageType.TEXT, 
				tableCells[0], 
				DB_NAME, 
				MessagePriority.NORMAL, 
				tableCells[3]);
		m.setSubject(tableCells[2]);
		
		setChanged();
		notifyObservers(m);
	}

	/**********************************************************************
	 * DELETING A ROW FROM THE DATABASE TABLE
	 * 
	 * This is an example of how to delete a row from a database table using
	 * this class. In most cases, this method probably does not need to be
	 * rewritten.
	 * 
	 * @param rowID
	 *            the SQLite database identifier for the row to delete.
	 */
	public void deleteRow(long rowID) {
		// ask the database manager to delete the row of given id
		try {
			db.delete("message", "msgId =" + rowID, null);
		} catch (Exception e) {
			Log.e("DB ERROR", e.toString());
			e.printStackTrace();
		}
		setChanged();
		notifyObservers();
	}

	/**********************************************************************
	 * UPDATING A ROW IN THE DATABASE TABLE
	 * 
	 * This is an example of how to update a row in the database table using
	 * this class. You should edit this method to suit your needs.
	 * 
	 * @param rowID
	 *            the SQLite database identifier for the row to update.
	 * @param rowStringOne
	 *            the new value for the row's first column
	 * @param rowStringTwo
	 *            the new value for the row's second column
	 */
	public void updateRow(long rowID, String[] tableCells) {
		// this is a key value pair holder used by android's SQLite functions
		ContentValues values = new ContentValues();
		values.put("srcUser", tableCells[0]);
		values.put("rDate", tableCells[1]);
		values.put("subject", tableCells[2]);
		values.put("mData", tableCells[3]);


		// ask the database object to update the database row of given rowID
		try {
			db.update("message", values, "msgId=" + rowID, null);
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
	public ArrayList<Message> getRowAsArray() {
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
	}
	public ArrayList<Message> getAllRowsAsArrays()
	{
		// create an ArrayList that will hold all of the data collected from
		// the database.
		ArrayList<Message> dataArrays = new ArrayList<Message>();

		// this is a database call that creates a "cursor" object.
		// the cursor object store the information collected from the
		// database and is used to iterate through the data.
		Cursor cursor;

		try
		{
			// ask the database object to create the cursor.

			cursor = db.query("message", new String[] { "msgId",
					"srcUser","rDate","subject","mData"}, null,
					null, null, null, null);

			// move the cursor's pointer to position zero.
			cursor.moveToFirst();

			// if there is data after the current cursor position, add it
			// to the ArrayList.
			if (!cursor.isAfterLast())
			{
				do
				{
					Message m = new TextMessage(MessageType.TEXT, 
							cursor.getString(1), 
							DB_NAME, 
							MessagePriority.NORMAL, 
							cursor.getString(4));
					m.setSubject(cursor.getString(3));
					dataArrays.add(m);
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

	/**********************************************************************
	 * RETRIEVING ALL ROWS FROM THE DATABASE TABLE
	 * 
	 * This is an example of how to retrieve all data from a database table
	 * using this class. You should edit this method to suit your needs.
	 * 
	 * the key is automatically assigned by the database
	 */

	/**********************************************************************
	 * THIS IS THE BEGINNING OF THE INTERNAL SQLiteOpenHelper SUBCLASS.
	 * 
	 * I MADE THIS CLASS INTERNAL SO I CAN COPY A SINGLE FILE TO NEW APPS AND
	 * MODIFYING IT - ACHIEVING DATABASE FUNCTIONALITY. ALSO, THIS WAY I DO NOT
	 * HAVE TO SHARE CONSTANTS BETWEEN TWO FILES AND CAN INSTEAD MAKE THEM
	 * PRIVATE AND/OR NON-STATIC. HOWEVER, I THINK THE INDUSTRY STANDARD IS TO
	 * KEEP THIS CLASS IN A SEPARATE FILE.
	 *********************************************************************/

	/**
	 * This class is designed to check if there is a database that currently
	 * exists for the given program. If the database does not exist, it creates
	 * one. After the class ensures that the database exists, this class will
	 * open the database for use. Most of this functionality will be handled by
	 * the SQLiteOpenHelper parent class. The purpose of extending this class is
	 * to tell the class how to create (or update) the database.
	 * 
	 * @author Randall Mitchell
	 * 
	 */
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

			/*
			 * String newTableQueryString = "create table " + TABLE_NAME + " ("
			 * + TABLE_ROW_ID + " integer primary key autoincrement not null," +
			 * TABLE_ROW_ONE + " text," + TABLE_ROW_TWO + " text" + ");";
			 */
			// execute the query string to the database.
			db.execSQL(messageTableQueryString);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// NOTHING TO DO HERE. THIS IS THE ORIGINAL DATABASE VERSION.
			// OTHERWISE, YOU WOULD SPECIFIY HOW TO UPGRADE THE DATABASE.
		}
	}
}