package tddd36.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import javax.net.ssl.SSLSocket;

import raddar.enums.NotificationType;
import raddar.models.MapObjectMessage;
import raddar.models.Message;
import raddar.models.NotificationMessage;

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
	public static void evaluateUser(String username, String password, SSLSocket so) {
		// Om anv�ndaren har loggat in med korrekt l�senord
		PrintWriter pw;
		if (Database.evalutateUser(username, password)) {
			try {
				// Skapa utstr�mmen till klienten
				pw = new PrintWriter(so.getOutputStream(), true);
				if(Server.onlineUsers.getUserAddress(username)!=null){
					System.out.println(username+" har f�rs�kt logga in, men �r redan inloggad p� ip-adressen "
							+ Server.onlineUsers.getUserAddress(username));
					if(Server.onlineUsers.getUserAddress(username).equals(so.getInetAddress())){
						pw.println("OK");
						pw.close();
						return;
					}else{
						NotificationMessage nm = (new NotificationMessage("Server", NotificationType.DISCONNECT));
						nm.setData("En annan klient har loggat in p� denna anv�ndare. Du kommer nu att loggas ut.");
						new Sender(nm, username);
						pw.println("OK_FORCE_LOGOUT");
						Server.onlineUsers.removeUser(username);
					}
				}else{
					// Svara med att det �r OK
					pw.println("OK");
				}
				pw.close();
				System.out.println(username + " har loggat in (" + so.getInetAddress().getHostAddress() + ") ");
				
				// L�gg till anv�ndaren i listan �ver inloggade anv�ndare

				Server.onlineUsers.addUser(username, so.getInetAddress());
				System.out.println("Användare online: "+Server.onlineUsers.onlineUsers());
			} catch (IOException e) {
				System.out.println("Could not respond with \"OK\" to client attempting to Log in. Socket error? ");
				e.printStackTrace();
			}
		} else {	// H�r hamnar vi n�r anv�ndarnanm eller l�senord �r fel
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
	 * Logga ut en anv�ndare och avassociera dennes IP-adress
	 * 
	 * @param username Anv�ndaren som ska loggas ut
	 */
	public static void logoutUser(String username) {
		if(username==null) return;
		MapObjectMessage mom = Database.getMapObject(username);
		InetAddress a = Server.onlineUsers.removeUser(username);
		if(mom != null){
			Database.removeMapObject(username);
			broadcast(mom);
		}
		// Kolla om anv�ndaren redan �r utloggad
		if (a == null)
			System.out.println(username + " �r redan utloggad ");
		// ...annars loggar vi ut denne.
		else			
			System.out.println(username + " har loggat ut (" + a.getHostAddress() + ") ");
	}
	private static void broadcast(Message m) {
		for (InetAddress adr: Server.onlineUsers.getAllAssociations().values()){
			new Sender(m, adr, 4043);
		}

	}
}
