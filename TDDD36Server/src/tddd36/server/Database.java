package tddd36.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import raddar.enums.MessageType;

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
			Connection con = DriverManager.getConnection(url, dbuser,
					dbpassword);
			return con.createStatement();
		} catch (SQLException ex) {
			System.out.println("Kunde inte ansluta till databasen. Kollat Library efter JDBC Plugin? ");
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
		boolean response = false;
		
		try {
			Statement st = openConnection();
			ResultSet rs = st.executeQuery("SELECT * FROM users WHERE userName = \'" + username + "\';");
			
			if (rs.next()) {
				if (password.equals(rs.getString(3)))
					response = true;
			}
			
		} catch (SQLException ex) {
			System.out.println("Fel syntax i MySQL-queryn i evaluateUser(). ");
		}
		return response;
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
			Statement st = openConnection();
			st.executeUpdate("INSERT INTO users VALUES (idusers, \'" + 
					username + "\', \'" + password + "\', \'" + level + "\', \'" + group + "\');");
		} catch (SQLException ex) {
			System.out.println("Fel syntax i MySQL-queryn i addUser(). ");
		}
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
	 * H�mta alla registrerade anv�ndare
	 * 
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
	 */
	public static void storeTextMessage(TextMessage mes) {
		try {
			Statement st = openConnection();
			
			String query = "INSERT INTO messages VALUES (idmessages, \'" + mes.getType() + "\', \'" +
					mes.getSrcUser() + "\', \'" + mes.getDestUser() + "\', \'" +
					mes.getFormattedDate() + "\', \'" + mes.getSubject() + "\', \'" +
				    mes.getMessage() +  "\');";
			st.executeUpdate(query);
		} catch (SQLException ex) {
			System.out.println("Fel syntax i MySQL-queryn i storeTextMessage(). ");
		}
	}

	/**
	 * H�mta alla textmeddelanden fr�n en specifik avs�ndare
	 * 
	 * @param username Avs�ndaren
	 * @return En ArrayList med alla textmeddelanden fr�n username
	 */
	public static ArrayList<TextMessage> retrieveAllTextMessagesFrom(String username) {
		ArrayList<TextMessage> list = new ArrayList<TextMessage>();
		try {
			Statement st = openConnection();
			ResultSet rs = st.executeQuery("SELECT * FROM messages WHERE fromUser = \'" + username + "\';");
			
			while (rs.next()) { 
				TextMessage tm = new TextMessage(MessageType.TEXT, rs.getString(3), rs.getString(4));
				tm.setDate(rs.getString(5));
				tm.setSubject(rs.getString(6));
				tm.setMessage(rs.getString(7));
				list.add(tm);
			}
		} catch (SQLException ex) {
			System.out.println("Fel syntax i MySQL-queryn i getAllTextMessagesFrom(). ");
		}
		return list;
	}
	
	/**
	 * H�mta alla textmeddelanden till en viss mottagare
	 * 
	 * @param username Mottagaren
	 * @return En ArrayList med alla textmeddelanden till username
	 */
	public static ArrayList<TextMessage> retrieveAllTextMessagesTo(String username) {
		ArrayList<TextMessage> list = new ArrayList<TextMessage>();
		try {
			Statement st = openConnection();
			ResultSet rs = st.executeQuery("SELECT * FROM messages WHERE toUser = \'" + username + "\';");
			
			while (rs.next()) { 
				TextMessage tm = new TextMessage(MessageType.TEXT, rs.getString(3), rs.getString(4));
				tm.setDate(rs.getString(5));
				tm.setSubject(rs.getString(6));
				tm.setMessage(rs.getString(7));
				list.add(tm);
			}
		} catch (SQLException ex) {
			System.out.println("Fel syntax i MySQL-queryn i getAllTextMessagesFrom(). ");
		}
		return list;
	}

	/**
	 * H�mta alla textmeddelanden i databasen
	 * 
	 * @return En ArrayList med alla textmeddelanden i databasen
	 */
	public static ArrayList<TextMessage> retrieveAllTextMessages() {
		ArrayList<TextMessage> list = new ArrayList<TextMessage>();
		try {
			Statement st = openConnection();
			ResultSet rs = st.executeQuery("SELECT * FROM messages;");
			
			while (rs.next()) { 
				TextMessage tm = new TextMessage(MessageType.TEXT, rs.getString(3), rs.getString(4));
				tm.setDate(rs.getString(5));
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
	public static ArrayList<TextMessage> retrieveAllTextMessagesOnDay(String date) {
		ArrayList<TextMessage> list = new ArrayList<TextMessage>();
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
}