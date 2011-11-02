package tddd36.server;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;

public class Associations {

	private HashMap<String, InetAddress> associations = new HashMap<String, InetAddress>();
	
	// Lägg till en användare i listan
	public InetAddress addUser(String username, InetAddress address) {
		return associations.put(username, address);
	}
	
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
	
	// Ta bort en användare från listan
	public InetAddress removeUser(String username) {
		return associations.remove(username);
	}
	
	// Hämta IP-adressen som en användare är associerad med
	public InetAddress getUserAddress(String username) {
		return associations.get(username);
	}
	
}
