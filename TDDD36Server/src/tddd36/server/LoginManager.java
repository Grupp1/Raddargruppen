package tddd36.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class LoginManager {
	
	/**
	 * Verifiera att en användare som försöker logga in har skrivit in rätt
	 * användarnamn och rätt lösenord. Om inloggningsuppgifterna är giltiga
	 * så läggs användaren med dess IP-adress till i Serverns lista över
	 * associerade IP-adresser och ett "OK" skickas tillbaka till klienten.
	 * 
	 * Annars associeras ingenting och ett "NOT OK" skickas tillbaka till 
	 * klienten.
	 * 
	 * @param username Användarnamnet 
	 * @param password Lösenordet
	 * @param so Socket via kommunikationen sker
	 */
	public static void evaluateUser(String username, String password, Socket so) {
		// Om användaren har loggat in med korrekt lösenord
		PrintWriter pw;
		if (Database.evalutateUser(username, password)) {
			System.out.println(username + " is now associated with " + so.getInetAddress().getHostAddress());
			
			// Lägg till användaren i listan över inloggade användare
			Server.onlineUsers.addUser(username, so.getInetAddress());
			try {
				// Hämta det krypterade lösenordet
				String encryptedPassword = Database.getEncryptedPassword(username);
				
				// Hämta användarens salt
				String salt = Database.getSalt(username);
				
				// Skapa utströmmen till klienten
				pw = new PrintWriter(so.getOutputStream(), true);
				
				// Skicka det krypterade lösenordet och användarens salt tillbaka till användaren
				// så att denne kan lagra det lokalt på sin telefon
				pw.println(encryptedPassword);
				pw.println(salt);
				
				// Här någonstans borde även meddelanden som buffrats upp under tiden användaren 
				// varit offline skickas till användaren nu när denne loggat in
				pw.close();
			} catch (IOException e) {
				System.out.println("Could not respond with \"OK\" to client attempting to Log in. Socket error? ");
				e.printStackTrace();
			}
		} else {	// Här hamnar vi när användarnanm eller lösenord är fel
			System.out.println("Ogiltigt användarnamn eller lösenord. ");
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
	 * Logga ut en användare och avassociera dennes IP-adress
	 * 
	 * @param username Användaren som ska loggas ut
	 */
	public static void logoutUser(String username) {
		InetAddress a = Server.onlineUsers.removeUser(username);
		// Kolla om användaren redan är utloggad
		if (a == null)
			System.out.println(username + " isn\'t associated with any IP-adress. ");
		// ...annars loggar vi ut denne.
		else			
			System.out.println(username + " is no longer associated with " + a.getHostAddress());
	}
}
