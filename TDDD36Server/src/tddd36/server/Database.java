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
	 * Skicka in användarnamn och lösenord för att se om det stämmer med vad
	 * som finns registrerat i databasen. 
	 * 
	 * @param username Användarnamnet
	 * @param password Lösenordet
	 * @return true om lösenorden stämmer överens, false annars
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
	 * Lägg till en godkänd användare med lösenord, användarnivå och användargrupp
	 * i databasen
	 * 
	 * @param username Användarnamnet
	 * @param password Användarens lösenord
	 * @param level Användarens behörighetsnivå
	 * @param group Användarens grupp
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
	 * Hämta en användares behörighetsnivå
	 *  
	 * @param username Användaren vars behörighetsnivå vi vill hämta
	 * @return Användarens behörighetsnivå, eller null om användaren inte finns
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
	 * Hämta en användarens användargrupp
	 * 	
	 * @param username Användaren
	 * @return Användarens grupp, eller null om användaren inte finns
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
	 * Hämta en användarens ID-nummer
	 * 
	 * @param username Användaren vars ID-nummer vi vill hämta
	 * @return Användarens ID-nummer, eller 0 om användaren inte finns
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
	 * Hämta användarnamnet som ett ID-nummer hör till
	 * 
	 * @param ID ID-numret som användarnamnet är kopplat till
	 * @return Användarnamnet som är kopplat till ID-numret, eller null om ID-numret är ogiltigt/inte finns
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


	/*public static void storeTextMessage(TextMessage mes) {
		try {
			Statement st = openConnection();
			
			//Syntaxen i MySQL funkar, men inte från java, suger >.<
			
			System.out.println("skriver ut sql inlägg sträng: " + "INSERT INTO messages VALUES (idmessages, \'" + mes.getType() + "\', \'" +
					mes.getSrcUser() + "\', \'" + mes.getDestUser() + "\', \'" +
					"2011-11-07 01:02:02" + "\', \'" + mes.getSubject() + "\', \'" +
				    mes.getMessage() +  "\');" );
			
			
			
			st.executeUpdate("skriver ut sql inlägg sträng: " + "INSERT INTO messages VALUES (idmessages, \'" + mes.getType() + "\', \'" +
					mes.getSrcUser() + "\', \'" + mes.getDestUser() + "\', \'" +
					"2011-11-07 01:02:02" + "\', \'" + mes.getSubject() + "\', \'" +
				    mes.getMessage() +  "\');" );
		} catch (SQLException ex) {
			System.out.println("Fel syntax i MySQL-queryn i storeTextMessage(). ");
		}
	}
*/
	
/*	public static List<TextMessage> getAllTextMessagesFrom(String username) {

		
		// Upptäckte att det inte går att söka i kolumnerna from och to men resten går, så det här suger.
		 
		
		try {
			Statement st = openConnection();
			ResultSet rs = st.executeQuery("SELECT * FROM messages WHERE from = \'" + username + "\';");
			
			if (rs.next()) 
				//den ska väl sparas till en lista här...
				return rs.getString(7); //ingen aning här, nog inte rätt kod, vill returnera hela raden inte bara meddelandet
		} catch (SQLException ex) {
			System.out.println("Fel syntax i MySQL-queryn i getAllTextMessagesFrom(). ");
		}
		return null;
	}*/
	

/*	public static List<TextMessage> getAllTextMessagesTo(String username) {

		 
  		 // Upptäckte att det inte går att söka i kolumnerna from och to men resten går, så det här suger.
		
	
	try {
			Statement st = openConnection();
			ResultSet rs = st.executeQuery("SELECT * FROM messages WHERE to = \'" + username + "\';");
			
			if (rs.next()) 
				//den ska väl sparas till en lista här...
				return rs.getString(7); //ingen aning här, nog inte rätt kod, vill returnera hela raden inte bara meddelandet
		} catch (SQLException ex) {
			System.out.println("Fel syntax i MySQL-queryn i getAllTextMessagesFrom(). ");
		}
		return null;


	}*/

	/*
	 * Privat metod för att enkelt kunna ansluta till databasen.
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
