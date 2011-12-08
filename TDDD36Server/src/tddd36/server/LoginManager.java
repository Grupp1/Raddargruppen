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
	 * Verifiera att en anvï¿½ndare som fï¿½rsï¿½ker logga in har skrivit in rï¿½tt
	 * anvï¿½ndarnamn och rï¿½tt lï¿½senord. Om inloggningsuppgifterna ï¿½r giltiga
	 * sï¿½ lï¿½ggs anvï¿½ndaren med dess IP-adress till i Serverns lista ï¿½ver
	 * associerade IP-adresser och ett "OK" skickas tillbaka till klienten.
	 * 
	 * Annars associeras ingenting och ett "NOT OK" skickas tillbaka till 
	 * klienten.
	 * 
	 * @param username Anvï¿½ndarnamnet
	 * @param password Lï¿½senordet
	 * @param so Socket via kommunikationen sker
	 */
	public static void evaluateUser(String username, String password, SSLSocket so) {
		// Om anvï¿½ndaren har loggat in med korrekt lï¿½senord
		PrintWriter pw;

		if (Database.evalutateUser(username, password)) {
			try {
				// Skapa utstrï¿½mmen till klienten
				pw = new PrintWriter(so.getOutputStream(), true);
				if(Server.onlineUsers.getUserAddress(username)!=null){
					System.out.println(username+" har fï¿½rsï¿½kt logga in, men ï¿½r redan inloggad pï¿½ ip-adressen "
							+ Server.onlineUsers.getUserAddress(username));
					if(Server.onlineUsers.getUserAddress(username).equals(so.getInetAddress())){
						pw.println("OK");
						pw.close();
						return;
					}else{
						NotificationMessage nm = (new NotificationMessage("Server", NotificationType.DISCONNECT));
						nm.setData("En annan klient har loggat in pï¿½ denna anvï¿½ndare. Du kommer nu att loggas ut.");
						new Sender(nm, username);
						pw.println("OK_FORCE_LOGOUT");
						Server.onlineUsers.removeUser(username);
					}
				}else{
					// Svara med att det ï¿½r OK
					pw.println("OK");
				}
				pw.close();
				System.out.println(username + " har loggat in (" + so.getInetAddress().getHostAddress() + ") ");
				
				// Lï¿½gg till anvï¿½ndaren i listan ï¿½ver inloggade anvï¿½ndare

				Server.onlineUsers.addUser(username, so.getInetAddress());
				System.out.println("AnvÃ¤ndare online: "+Server.onlineUsers.onlineUsers());
			} catch (IOException e) {
				System.out.println("Could not respond with \"OK\" to client attempting to Log in. Socket error? ");
				e.printStackTrace();
			}
		} else {	// Hï¿½r hamnar vi nï¿½r anvï¿½ndarnanm eller lï¿½senord ï¿½r fel
			System.out.println("Ogiltigt anvï¿½ndarnamn eller lï¿½senord. ");
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
	 * Logga ut en anvï¿½ndare och avassociera dennes IP-adress
	 * 
	 * @param username Anvï¿½ndaren som ska loggas ut
	 */
	public static void logoutUser(String username) {
		if(username==null) return;
		MapObjectMessage mom = Database.getMapObject(username);
		InetAddress a = Server.onlineUsers.removeUser(username);
		if(mom != null){
			Database.removeMapObject(username);
			broadcast(mom);
		}
		// Kolla om anvï¿½ndaren redan ï¿½r utloggad
		if (a == null)
			System.out.println(username + " är redan utloggad ");
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
