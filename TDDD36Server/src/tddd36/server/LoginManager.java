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
			try {
				// Skapa utstr�mmen till klienten
				pw = new PrintWriter(so.getOutputStream(), true);
				
				// Svara med att det �r OK
				pw.println("OK");
				pw.close();
				
				System.out.println(username + " is now associated with " + so.getInetAddress().getHostAddress());
				
				// L�gg till anv�ndaren i listan �ver inloggade anv�ndare
				Server.onlineUsers.addUser(username, so.getInetAddress());
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
			System.out.println(username + " �r inte associerad med n�gon IP-adress. ");
		// ...annars loggar vi ut denne.
		else			
			System.out.println(username + " �r inte l�ngre associerad med " + a.getHostAddress());
	}
}
