package tddd36.server;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;

public class Associations {

	private HashMap<String, InetAddress> associations = new HashMap<String, InetAddress>();
	
	/**
	 * L�gg till en anv�ndare och tillsammans med dennes InetAddress
	 * 
	 * @param username Anv�ndarens anv�ndarnamn
	 * @param address Anv�ndarens InetAddress
	 * @return Anv�ndarens gamla InetAddress, eller null om den inte hade n�gon tidigare
	 */
	public InetAddress addUser(String username, InetAddress address) {
		return associations.put(username, address);
	}
	
	/**
	 * L�gg till en anv�ndare och tillsammans med dennes IP-adress
	 * 
	 * @param username Anv�ndarens anv�ndarnamn
	 * @param address IP-adressen (t ex: 130.236.188.23)
	 * @return Anv�ndarens gamla InetAddress, eller null om den inte hade n�gon tidigare
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
	 * Ta bort en anv�ndare fr�n listan
	 * 
	 * @param username Anv�ndaren som ska tas bort fr�n listan
	 * @return InetAddressen som var associerad med anv�ndaren
	 */
	public InetAddress removeUser(String username) {
		return associations.remove(username);
	}
	
	/**
	 * H�mta en anv�ndares InetAddress
	 * 
	 * @param username Anv�ndaren vars InetAddress man vill h�mta
	 * @return InetAddressen som �r associerad med username
	 */
	public InetAddress getUserAddress(String username) {
		return associations.get(username);
	}
	
	/**
	 * Kolla hur m�nga anv�ndare som �r online och associerade
	 * @return Antalet anv�ndare som �r online
	 */
	public int onlineUsers() {
		return associations.size();
	}
	
	/**
	 * H�mta listan av online anv�ndare
	 * @return HashMapen med alla online anv�ndare
	 */
	public HashMap<String, InetAddress> getAllAssociations() {
		return associations;
	}
	
}
