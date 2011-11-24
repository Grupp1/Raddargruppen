package tddd36.server;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;

public class Associations {

	private HashMap<String, InetAddress> associations = new HashMap<String, InetAddress>();
	
	/**
	 * Lägg till en användare och tillsammans med dennes InetAddress
	 * 
	 * @param username Användarens användarnamn
	 * @param address Användarens InetAddress
	 * @return Användarens gamla InetAddress, eller null om den inte hade någon tidigare
	 */
	public InetAddress addUser(String username, InetAddress address) {
		return associations.put(username, address);
	}
	
	/**
	 * Lägg till en användare och tillsammans med dennes IP-adress
	 * 
	 * @param username Användarens användarnamn
	 * @param address IP-adressen (t ex: 130.236.188.23)
	 * @return Användarens gamla InetAddress, eller null om den inte hade någon tidigare
	 */
	public InetAddress adduser(String username, String address) {
		try {
			// Testa skapa ett InetAddress-objekt av given adress
			InetAddress adr = InetAddress.getByName(address);
			return addUser(username, adr);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return null;
		}		
	}
	
	/**
	 * Ta bort en användare från listan
	 * 
	 * @param username Användaren som ska tas bort från listan
	 * @return InetAddressen som var associerad med användaren
	 */
	public InetAddress removeUser(String username) {
		return associations.remove(username);
	}
	
	/**
	 * Hämta en användares InetAddress
	 * 
	 * @param username Användaren vars InetAddress man vill hämta
	 * @return InetAddressen som är associerad med username
	 */
	public InetAddress getUserAddress(String username) {
		return associations.get(username);
	}
	
	/**
	 * Kolla hur många användare som är online och associerade
	 * @return Antalet användare som är online
	 */
	public int onlineUsers() {
		return associations.size();
	}
	
	/**
	 * Hämta listan av online användare
	 * @return HashMapen med alla online användare
	 */
	public HashMap<String, InetAddress> getAllAssociations() {
		return associations;
	}
	
}
