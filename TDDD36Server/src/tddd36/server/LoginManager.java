package tddd36.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class LoginManager {
	
	/**
	 * Verifiera att en anv�ndare som f�rs�ker logga in har skrivit in r�tt
	 * anv�ndarnamn och r�tt l�senord. Om inloggningsuppgifterna �r giltiga
	 * s� l�ggs anv�ndaren med dess IP-adress till i Serverns lista �ver
	 * associerade IP-adresser och ett "OK" skickas tillbaka till klienten.
	 * 
	 * Annars associeras ingenting och ett "NOT OK" skickas tillbaka till 
	 * klienten.
	 * 
	 * @param username Anv�ndarnamnet 
	 * @param password L�senordet
	 * @param so Socket via kommunikationen sker
	 */
	public static void evaluateUser(String username, String password, Socket so) {
		// Om anv�ndaren har loggat in med korrekt l�senord
		PrintWriter pw;
		if (Database.evalutateUser(username, password)) {
			System.out.println(username + " is now associated with " + so.getInetAddress().getHostAddress());
			
			// L�gg till anv�ndaren i listan �ver inloggade anv�ndare
			Server.onlineUsers.addUser(username, so.getInetAddress());
			try {
				// H�mta det krypterade l�senordet
				String encryptedPassword = Database.getEncryptedPassword(username);
				
				// H�mta anv�ndarens salt
				String salt = Database.getSalt(username);
				
				// Skapa utstr�mmen till klienten
				pw = new PrintWriter(so.getOutputStream(), true);
				
				// Skicka det krypterade l�senordet och anv�ndarens salt tillbaka till anv�ndaren
				// s� att denne kan lagra det lokalt p� sin telefon
				pw.println(encryptedPassword);
				pw.println(salt);
				
				// H�r n�gonstans borde �ven meddelanden som buffrats upp under tiden anv�ndaren 
				// varit offline skickas till anv�ndaren nu n�r denne loggat in
				pw.close();
			} catch (IOException e) {
				System.out.println("Could not respond with \"OK\" to client attempting to Log in. Socket error? ");
				e.printStackTrace();
			}
		} else {	// H�r hamnar vi n�r anv�ndarnanm eller l�senord �r fel
			System.out.println("Ogiltigt anv�ndarnamn eller l�senord. ");
			try {
				pw = new PrintWriter(so.getOutputStream(), true);
				pw.println("NOT OK");
				pw.close();
			} catch (IOException e) {
				System.out.println("Could not respond with \"NOT OK\" to client attempting to Log in. Socket error?");
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Logga ut en anv�ndare och avassociera dennes IP-adress
	 * 
	 * @param username Anv�ndaren som ska loggas ut
	 */
	public static void logoutUser(String username) {
		InetAddress a = Server.onlineUsers.removeUser(username);
		// Kolla om anv�ndaren redan �r utloggad
		if (a == null)
			System.out.println(username + " isn\'t associated with any IP-adress. ");
		// ...annars loggar vi ut denne.
		else			
			System.out.println(username + " is no longer associated with " + a.getHostAddress());
	}
}
