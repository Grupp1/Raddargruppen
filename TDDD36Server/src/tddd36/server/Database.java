package tddd36.server;



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import raddar.enums.MapOperation;
import raddar.enums.MessageType;
import raddar.models.ContactMessage;
import raddar.models.Encryption;
import raddar.models.MapObjectMessage;
import raddar.models.Message;
import raddar.models.TextMessage;


/**
 * -- ANV�NDARGR�NSSNITT MOT DATABASEN --
 * 
 * Metoderna i denna klass ska anv�ndas n�r man beh�ver komma �t MySQL-databasen p� servern.
 * Skriv inte egna queries f�r att accessa databasen. 
 * 
 * @author kjeka190, andbo265
 *
 */
public class Database {

	private static String url = "jdbc:mysql://db-und.ida.liu.se:3306/tddd36_proj1";
	private static String dbuser = "tddd36_proj1";
	private static String dbpassword = "tddd36_proj1_17a8";

	/*
	 * Privat metod f�r att enkelt kunna ansluta till databasen.
	 */
	private static Statement openConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection(url, dbuser,
					dbpassword);
			return con.createStatement();
		} catch (SQLException ex) {
			System.out.println("Kunde inte ansluta till databasen. Kollat Library efter JDBC Plugin? ");
		} catch (ClassNotFoundException e) {
			System.out.println("Fel i i Class.forname()-anropet, kollat så att GSON grejerna finns i Library?");
		}
		return null;
	}

	/**
	 * Skicka in anv�ndarnamn och l�senord f�r att se om det st�mmer med vad
	 * som finns registrerat i databasen. 
	 * 
	 * @param username Anv�ndarnamnet
	 * @param password L�senordet
	 * @return true om l�senorden st�mmer �verens, false annars
	 */
	public static boolean evalutateUser(String username, String password) {
		try {
			Statement st = openConnection();
			ResultSet rs = st.executeQuery("SELECT * FROM users WHERE userName = \'" + username + "\';");

			if (rs.next()) {
				if (password == null) {
					System.out.println("Lösenordet får inte vara null (Database.java). ");
					return false;
				}
				// J�mf�r input l�senordet med det lagrade l�senordet (b�da �r krypterade)
				if (password.equals(rs.getString("password"))) {
					return true;
				}
			}
		} catch (SQLException ex) {
			System.out.println("Fel syntax i MySQL-queryn i evaluateUser(). ");
		}
		return false;
	}

	/**
	 * L�gg till en godk�nd anv�ndare med l�senord, anv�ndarniv� och anv�ndargrupp
	 * i databasen
	 * 
	 * @param username Anv�ndarnamnet
	 * @param password Anv�ndarens l�senord
	 * @param level Anv�ndarens beh�righetsniv�
	 * @param group Anv�ndarens grupp
	 */
	public static void addUser(String username, String password, char level,
			String group) {
		try {
			// Skapa ett nytt salt f�r denna anv�ndaren
			String salt = Encryption.newSalt();
			// Salta och hasha l�senordet innan det l�ggs in i databasen
			password = Encryption.encrypt(password, salt);
			Statement st = openConnection();
			st.executeUpdate("INSERT INTO users VALUES (idusers, \'" + 
					username + "\', \'" + password + "\', \'" + level + "\', \'" + group + "\', \'" + salt + "\');");
		} catch (SQLException ex) {
			System.out.println("Fel syntax i MySQL-queryn i addUser(). "+ex);
		}
	}

	public static boolean addMapObject(MapObjectMessage mo){
		try{
			Statement st = openConnection();
			st.executeUpdate("INSERT INTO map_objects VALUES (idmap_objects, \'"+
					mo.getClassName()+ "\', \'"+mo.getJson()+"\', \'"+mo.getId()+"\');");
			return true;

		}catch(SQLException ex){
			return false;
		}
	}
	public static void removeMapObject(String id){
		try{
			Statement st = openConnection();
			st.executeUpdate("DELETE FROM map_objects WHERE map_id = \'"+id+ "\';");

		}catch(SQLException ex){
			System.out.println("Fel syntax i MySQL-queryn i removeMapObject. "+ex);
		}
	}
	public static void updateMapObject(MapObjectMessage mo){

		try{
			Statement st = openConnection();
			st.executeUpdate("UPDATE map_objects SET class_name = \'"+mo.getClassName()+"\', "+
					"json_string = \'"+mo.getJson()+"\' WHERE map_id = \'"+mo.getId()+"\';");	

		}catch(SQLException ex){
			System.out.println("Fel syntax i MySQL-queryn i updateMapObject. "+ex);
		}
	}
	public static MapObjectMessage getMapObject(String id) {
		try {
			Statement st = openConnection();
			ResultSet rs = st.executeQuery("SELECT * FROM map_objects WHERE map_id = \'"+id+ "\';");
			if(rs.next())
				return new MapObjectMessage(rs.getString(3), rs.getString(2), rs.getString(4),
						MapOperation.REMOVE,"");
		} catch (SQLException ex) {
			System.out.println("Fel syntax i MySQL-queryn i getMapObject(). "+ex);
		}
		return null;
	}

	/**
	 * H�mta en anv�ndares salt
	 * @param username Anv�ndaren
	 * @return Anv�ndarens salt
	 */
	public static String getSalt(String username) {
		try {
			Statement st = openConnection();
			ResultSet rs = st.executeQuery("SELECT * FROM users WHERE userName = \'" + username + "\';");

			if (rs.next()) 
				return rs.getString("salt");
		} catch (SQLException ex) {
			System.out.println("Fel syntax i MySQL-queryn i getSalt(). ");
		}
		return null;
	}

	/**
	 * H�mtar en anv�ndares krypterade l�senord. Denna metoden finns endast f�r att vi ska kunna
	 * lagra det krypterade l�senordet p� klienten. Annars beh�vs inte denna (ska inte beh�vas...)
	 * @param username Anv�ndaren
	 * @return Anv�ndarens krypterade l�senord
	 */
	public static String getEncryptedPassword(String username) {
		try {
			Statement st = openConnection();
			ResultSet rs = st.executeQuery("SELECT * FROM users WHERE userName = \'" + username + "\';");

			if (rs.next()) 
				return rs.getString("password");
		} catch (SQLException ex) {
			System.out.println("Fel syntax i MySQL-queryn i getSalt(). ");
		}
		return null;
	}

	/**
	 * H�mta en anv�ndares beh�righetsniv�
	 *  
	 * @param username Anv�ndaren vars beh�righetsniv� vi vill h�mta
	 * @return Anv�ndarens beh�righetsniv�, eller null om anv�ndaren inte finns
	 */
	public static String getUserLevel(String username) {
		try {
			Statement st = openConnection();
			ResultSet rs = st.executeQuery("SELECT * FROM users WHERE userName = \'" + username + "\';");

			if (rs.next()) 
				return rs.getString(4);
		} catch (SQLException ex) {
			System.out.println("Fel syntax i MySQL-queryn i getUserLevel(). ");
		}
		return null;
	}

	/**
	 * H�mta en anv�ndarens anv�ndargrupp
	 * 	
	 * @param username Anv�ndaren
	 * @return Anv�ndarens grupp, eller null om anv�ndaren inte finns
	 */
	public static String getUserGroup(String username) {
		try {
			Statement st = openConnection();
			ResultSet rs = st.executeQuery("SELECT * FROM users WHERE userName = \'" + username + "\';");

			if (rs.next()) 
				return rs.getString(5);
		} catch (SQLException ex) {
			System.out.println("Fel syntax i MySQL-queryn i getUserGroup(). ");
		}
		return null;
	}

	/**
	 * H�mta en anv�ndarens ID-nummer
	 * 
	 * @param username Anv�ndaren vars ID-nummer vi vill h�mta
	 * @return Anv�ndarens ID-nummer, eller 0 om anv�ndaren inte finns
	 */
	public static int getUserID(String username) {
		try {
			Statement st = openConnection();
			ResultSet rs = st.executeQuery("SELECT * FROM users WHERE userName = \'" + username + "\';");

			if (rs.next()) 
				return Integer.parseInt(rs.getString(1));
		} catch (SQLException ex) {
			System.out.println("Fel syntax i MySQL-queryn i getUserID(). ");
		}
		return 0;
	}

	/**
	 * H�mta anv�ndarnamnet som ett ID-nummer h�r till
	 * 
	 * @param ID ID-numret som anv�ndarnamnet �r kopplat till
	 * @return Anv�ndarnamnet som �r kopplat till ID-numret, eller null om ID-numret �r ogiltigt/inte finns
	 */
	public static String getUserName(int ID) {
		try {
			Statement st = openConnection();
			ResultSet rs = st.executeQuery("SELECT * FROM users WHERE idusers = \'" + ID + "\';");

			if (rs.next()) 
				return rs.getString(2);
		} catch (SQLException ex) {
			System.out.println("Fel syntax i MySQL-queryn i getUsername(). ");
		}
		return null;
	}
	/**
	 * Tar bort meddelande fr�n buffern
	 * @param toUser anv�ndaren vilkens meddelande skall tas bort
	 */
	public static void deleteFromBuffer(String toUser){
		try{
			Statement st = openConnection();
			st.executeUpdate("DELETE FROM bufferedmessages WHERE toUser = \'" 
					+ toUser + "\';");
		} catch(SQLException ex){
			System.out.println("Fel i deleteFromBuffer"+ ex);
		}
	}
	/**
	 * Tar bort meddelande fr�n messages databasem
	 * @param tm Meddelanden d� vill ta bort
	 */	
	public static void deleteFromTextMessages(TextMessage tm){
		try{
			Statement st = openConnection();
			st.executeUpdate("DELETE FROM messages WHERE toUser = \'" 
					+ tm.getDestUser() + "\' and fromUser = \'"+tm.getSrcUser()+"\' and date = \'"
					+tm.getDate()+"\';");

		} catch(SQLException ex){
			System.out.println("Fel i deleteFromTextMessages "+ex);
		}
	}

	/**
	 * H�mta alla registrerade anv�ndare i en arraylist<string>
	 * (Vet inte om den h�r funktionen beh�vs egentligen men vet inte om den anv�nds s� l�ter den va)
	 * @return En ArrayList med alla registrerade anv�ndare
	 */

	public static ArrayList<String> getAllUsers() {
		ArrayList<String> list = new ArrayList<String>();
		try {
			Statement st = openConnection();
			ResultSet rs = st.executeQuery("SELECT * FROM users;");

			while (rs.next()) 
				list.add(rs.getString(2));

		} catch (SQLException ex) {
			System.out.println("Fel syntax i MySQL-queryn i getAllUsers(). ");
		}
		return list;
	}

	/**
	 * H�mta alla registrerade anv�ndare fr�n databasen p� servern
	 * 
	 * @return En ArrayList med alla registrerade anv�ndare som messages
	 */

	public static ArrayList<Message> retrieveAllUsers() {
		ArrayList<Message> lista = new ArrayList<Message>();
		try {
			Statement st = openConnection();
			ResultSet rs = st.executeQuery("SELECT * FROM users;");

			while (rs.next()) 
				lista.add(new ContactMessage(rs.getString(2)));

		} catch (SQLException ex) {
			System.out.println("Fel syntax i MySQL-queryn i retrieveAllUsers(). ");
		}
		return lista;
	}


	/**
	 * H�mta alla anv�ndare i en viss grupp
	 * 
	 * @param group Gruppen
	 * @return En ArrayList med alla anv�ndare i group
	 */
	public static ArrayList<String> getAllUsersInGroup(String group) {
		ArrayList<String> list = new ArrayList<String>();
		try {
			Statement st = openConnection();
			ResultSet rs = st.executeQuery("SELECT * FROM users WHERE userGroup = \'" + group + "\';");

			while (rs.next()) 
				list.add(rs.getString(2));

		} catch (SQLException ex) {
			System.out.println("Fel syntax i MySQL-queryn i getAllUsersInGroup(). ");
		}
		return list;
	}

	/**
	 * H�mta alla anv�ndare med en viss anv�ndarniv�
	 * 
	 * @param level Anv�ndarniv�n
	 * @return En ArrayList med alla anv�ndare med anv�ndarniv�n level
	 */
	public static ArrayList<String> getAllUsersWithUserLevel(String level) {
		ArrayList<String> list = new ArrayList<String>();
		try {
			Statement st = openConnection();
			ResultSet rs = st.executeQuery("SELECT * FROM users WHERE userLevel = \'" + level + "\';");

			while (rs.next()) 
				list.add(rs.getString(2));

		} catch (SQLException ex) {
			System.out.println("Fel syntax i MySQL-queryn i getAllUserWithUserLevel(). ");
		}
		return list;
	}

	/**
	 * Lagra ett textmeddelande
	 * 
	 * @param mes Textmeddelandet som ska lagras
	 * @param tableName Var textmeddelandet ska lagras
	 */
	public static void storeTextMessage(TextMessage mes) {
		try {
			Statement st = openConnection();

			String query = "INSERT INTO messages VALUES (idmessages, \'" + mes.getType() + "\', \'" +
					mes.getSrcUser() + "\', \'" + mes.getDestUser() + "\', \'" +
					mes.getDate() + "\', \'" + mes.getSubject() + "\', \'" +
					mes.getData() +  "\', \'0\');";
			st.executeUpdate(query);
		} catch (SQLException ex) {
			System.out.println("Fel syntax i MySQL-queryn i storeTextMessage(). "+ex);
		}
	}
	/**
	 * Lagrar ett Message i en buffer som t�mms n�r en anv�ndare loggar in
	 * @param mes
	 */
	public static void storeIntoBuffer(Message mes) {
		try {
			Statement st = openConnection();

			String query = "INSERT INTO bufferedmessages VALUES (idbuffered_messages, \'" + mes.getType() + "\', \'" +
					mes.getSrcUser() + "\', \'" + mes.getDestUser() + "\', \'" +
					mes.getDate() + "\', \'" + mes.getSubject() + "\', \'" +
					mes.getData() +  "\', \'0\');";
			st.executeUpdate(query);
		} catch (SQLException ex) {
			System.out.println("Fel syntax i MySQL-queryn i storeIntoBuffer(). "+ ex);
		}
	}


	/**
	 * H�mta buffrade meddelanden till en viss mottagare
	 * @param username Mottagarens anv�ndarnamn
	 * @return En arraylist med alla meddelanden till en person
	 */
	public static ArrayList<Message> retrieveAllBufferedMessagesTo(String username) {
		ArrayList<Message> list = new ArrayList<Message>();
		try {
			Statement st = openConnection();
			ResultSet rs = st.executeQuery("SELECT * FROM bufferedmessages WHERE toUser = \'" + username + "\';");

			while (rs.next()) { 
				TextMessage tm = new TextMessage(MessageType.convert(rs.getString(2)), rs.getString(3), rs.getString(4));
				tm.setDate(rs.getString(5));
				tm.setSubject(rs.getString(6));
				tm.setMessage(rs.getString(7));
				list.add(tm);
			}
		} catch (SQLException ex) {
			System.out.println("Fel syntax i MySQL-queryn i retrieveAllBufferedMessagesTo. ");
			System.out.println(ex);
		}
		return list;
	}

	/**
	 * H�mta alla textmeddelanden till en viss mottagare
	 * 
	 * @param username Mottagaren
	 * @return En ArrayList med alla textmeddelanden till username
	 */
	public static ArrayList<Message> retrieveAllTextMessagesTo(String username) {
		ArrayList<Message> list = new ArrayList<Message>();
		try {
			Statement st = openConnection();
			ResultSet rs = st.executeQuery("SELECT * FROM messages WHERE toUser = \'" + username + "\';");
			while (rs.next()) { 
				TextMessage tm = new TextMessage(MessageType.convert(rs.getString(2)), rs.getString(3), rs.getString(4));
				tm.setDate(rs.getString(5));
				tm.setSubject(rs.getString(6));
				tm.setMessage(rs.getString(7));
				list.add(tm);
			}
		} catch (SQLException ex) {
			System.out.println("Fel syntax i MySQL-queryn i getAllTextMessagesTo(). ");
		}
		return list;
	}

	/**
	 * H�mta alla textmeddelanden i databasen
	 * 
	 * @return En ArrayList med alla textmeddelanden i databasen
	 */
	public static ArrayList<Message> retrieveAllTextMessages() {
		ArrayList<Message> list = new ArrayList<Message>();
		try {
			Statement st = openConnection();
			ResultSet rs = st.executeQuery("SELECT * FROM messages;");

			while (rs.next()) { 
				TextMessage tm = new TextMessage(MessageType.TEXT, rs.getString(3), rs.getString(4));
				//	tm.setDate(rs.getString(5));
				tm.setSubject(rs.getString(6));
				tm.setMessage(rs.getString(7));
				list.add(tm);
			}
		} catch (SQLException ex) {
			System.out.println("Fel syntax i MySQL-queryn i getAllTextMessages(). ");
		}
		return list;
	}


	/**
	 * H�mta alla textmeddelanden som har skickats en specifik dag 
	 * 
	 * @param day En specifik dag i detta formatet: yyyy-mm-dd
	 * @return En ArrayList med alla textmeddelanden som skickats p� day
	 */
	public static ArrayList<Message> retrieveAllTextMessagesOnDay(String date) {
		ArrayList<Message> list = new ArrayList<Message>();
		try {
			Statement st = openConnection();
			ResultSet rs = st.executeQuery("SELECT * FROM messages WHERE date LIKE \'" + date + "%\';");

			while (rs.next()) { 
				TextMessage tm = new TextMessage(MessageType.TEXT, rs.getString(3), rs.getString(4));
				tm.setDate(rs.getString(5));
				tm.setSubject(rs.getString(6));
				tm.setMessage(rs.getString(7));
				list.add(tm);
			}
		} catch (SQLException ex) {
			System.out.println("Fel syntax i MySQL-queryn i getAllTextMessagesOnDay(). ");
		}
		return list;
	}

	public static ArrayList<Message> retrieveAllMapObjects() {
		ArrayList<Message> list = new ArrayList<Message>();
		try {
			Statement st = openConnection();
			ResultSet rs = st.executeQuery("SELECT * FROM map_objects;");

			while (rs.next()) { 
				MapObjectMessage tm = new MapObjectMessage(rs.getString(3), rs.getString(2), rs.getString(4),
						MapOperation.ADD,"");
				list.add(tm);
			}
		} catch (SQLException ex) {
			System.out.println("Fel syntax i MySQL-queryn i retrieveAllMapObjects(). ");
		}
		return list;
	}
}
