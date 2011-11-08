package tddd36.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

	private static String url = "jdbc:mysql://db-und.ida.liu.se:3306/tddd36_proj1";
	private static String dbuser = "tddd36_proj1";
	private static String dbpassword = "tddd36_proj1_17a8";

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
	public static String getUsername(int ID) {
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


	/*public static void storeTextMessage(TextMessage m) {

	}

	public static List<TextMessage> getAllTextMessagesFrom(String username) {

	}

	public static List<TextMessage> getAllTextMessagesTo(String username) {

	}*/

	/*
	 * Privat metod f�r att enkelt kunna ansluta till databasen.
	 */
	private static Statement openConnection() {
		try {
			Connection con = DriverManager.getConnection(url, dbuser,
					dbpassword);
			return con.createStatement();
		} catch (SQLException ex) {
			System.out.println("Kunde inte ansluta till databasen. ");
		}
		return null;
	}
	
}
