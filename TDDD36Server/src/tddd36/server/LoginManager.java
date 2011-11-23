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
			try {
				// Skapa utströmmen till klienten
				pw = new PrintWriter(so.getOutputStream(), true);
				
				// Svara med att det är OK
				pw.println("OK");
				pw.close();
				
				System.out.println(username + " is now associated with " + so.getInetAddress().getHostAddress());
				
				// Lägg till användaren i listan över inloggade användare
				Server.onlineUsers.addUser(username, so.getInetAddress());
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
			System.out.println(username + " är inte associerad med någon IP-adress. ");
		// ...annars loggar vi ut denne.
		else			
			System.out.println(username + " är inte längre associerad med " + a.getHostAddress());
	}
}
