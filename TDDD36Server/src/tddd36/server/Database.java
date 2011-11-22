package tddd36.server;



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import raddar.enums.MessageType;
import raddar.models.Encryption;
import raddar.models.TextMessage;


/**
 * -- ANVÄNDARGRÄNSSNITT MOT DATABASEN --
 * 
 * Metoderna i denna klass ska användas när man behöver komma åt MySQL-databasen på servern.
 * Skriv inte egna queries för att accessa databasen. 
 * 
 * @author kjeka190, andbo265
 *
 */
public class Database {

	private static String url = "jdbc:mysql://db-und.ida.liu.se:3306/tddd36_proj1";
	private static String dbuser = "tddd36_proj1";
	private static String dbpassword = "tddd36_proj1_17a8";
	
	/*
	 * Privat metod för att enkelt kunna ansluta till databasen.
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
			System.out.println("Fel i i Class.forname()-anropet");
		}
		return null;
	}

	/**
	 * Skicka in användarnamn och lösenord för att se om det stämmer med vad
	 * som finns registrerat i databasen. 
	 * 
	 * @param username Användarnamnet
	 * @param password Lösenordet
	 * @return true om lösenorden stämmer överens, false annars
	 */
	public static boolean evalutateUser(String username, String password) {
		try {
			Statement st = openConnection();
			ResultSet rs = st.executeQuery("SELECT * FROM users WHERE userName = \'" + username + "\';");
			
			if (rs.next()) {
				// Hämta saltet
				String salt = rs.getString("salt");
				if (salt == null) {
					System.out.println("Salt får inte vara null (Database.java). ");
					return false;
				}
				if (password == null) {
					System.out.println("Lösenordet får inte vara null (Database.java). ");
					return false;
				}
				// Kryptera det klara lösenordet
				password = Encryption.encrypt(password, salt);
				// Jämför med det lagrade, krypterade lösenordet
				if (password.equals(rs.getString("password"))) {
					System.out.println(username + " har angivit korrekt lösenord. ");
					return true;
				}
			}
		} catch (SQLException ex) {
			System.out.println("Fel syntax i MySQL-queryn i evaluateUser(). ");
		}
		return false;
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
			// Skapa ett nytt salt för denna användaren
			String salt = Encryption.newSalt();
			// Salta och hasha lösenordet innan det läggs in i databasen
			password = Encryption.encrypt(password, salt);
			Statement st = openConnection();
			st.executeUpdate("INSERT INTO users VALUES (idusers, \'" + 
					username + "\', \'" + password + "\', \'" + level + "\', \'" + group + "\', \'" + salt + "\');");
		} catch (SQLException ex) {
			System.out.println("Fel syntax i MySQL-queryn i addUser(). ");
		}
	}
	
	/**
	 * Hämta en användares salt
	 * @param username Användaren
	 * @return Användarens salt
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
	 * Hämtar en användares krypterade lösenord. Denna metoden finns endast för att vi ska kunna
	 * lagra det krypterade lösenordet på klienten. Annars behövs inte denna (ska inte behövas...)
	 * @param username Användaren
	 * @return Användarens krypterade lösenord
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
	 * Hämta alla registrerade användare
	 * 
	 * @return En ArrayList med alla registrerade användare
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
	 * Hämta alla användare i en viss grupp
	 * 
	 * @param group Gruppen
	 * @return En ArrayList med alla användare i group
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
	 * Hämta alla användare med en viss användarnivå
	 * 
	 * @param level Användarnivån
	 * @return En ArrayList med alla användare med användarnivån level
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
					mes.getDate() + "\', \'" + mes.getSubject() + "\', \'" +
				    mes.getData() +  "\');";
			st.executeUpdate(query);
		} catch (SQLException ex) {
			System.out.println("Fel syntax i MySQL-queryn i storeTextMessage(). ");
		}
	}

	/**
	 * Hämta alla textmeddelanden från en specifik avsändare
	 * 
	 * @param username Avsändaren
	 * @return En ArrayList med alla textmeddelanden från username
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
	 * Hämta alla textmeddelanden till en viss mottagare
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
	 * Hämta alla textmeddelanden i databasen
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
	 * Hämta alla textmeddelanden som har skickats en specifik dag 
	 * 
	 * @param day En specifik dag i detta formatet: yyyy-mm-dd
	 * @return En ArrayList med alla textmeddelanden som skickats på day
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
