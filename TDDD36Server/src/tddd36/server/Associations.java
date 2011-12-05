package tddd36.server;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import raddar.enums.MessageType;
import raddar.models.NotificationMessage;

import raddar.enums.OnlineOperation;
import raddar.models.OnlineUsersMessage;

public class Associations {


	private static HashMap<String, InetAddress> associations = new HashMap<String, InetAddress>();
	private HashMap<String, InetAddress> updatedAssociations = new HashMap<String, InetAddress>();

	Associations(){
		long delay = 15000;
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask(){
			@Override
			public void run() {
				// Ersätter listan med den uppdaterade från förra probe-sessionen
				associations.clear();
				associations.putAll(updatedAssociations);
				updatedAssociations.clear();
				
				// Skickar ut nya probes
				if(onlineUsers()>0){
					Set<String> keys = associations.keySet();
					for (String key: keys){
						System.out.println("Listan: "+key+", "+associations.get(key));
						NotificationMessage m = new NotificationMessage("server", null);
						m.setType(MessageType.PROBE);
						new Sender(m,associations.get(key));
					}
				}
			}
		}, delay, delay);
	}

	/**
	 * Lägg till en användare och tillsammans med dennes InetAddress
	 * och broadcasta att denna nu är online.
	 * 
	 * @param username Användarens användarnamn
	 * @param address Användarens InetAddress
	 * @return Användarens gamla InetAddress, eller null om den inte hade någon tidigare
	 */

	public InetAddress addUser(String userName, InetAddress address) {
		updatedAssociations.put(userName, address);
		Sender.broadcast(new OnlineUsersMessage(OnlineOperation.ADD, userName));
		return associations.put(userName, address);

	}

	/**
	 * Lägg till en användare och tillsammans med dennes IP-adress och
	 * broadcasta att denna nu är online
	 * 
	 * @param username Användarens användarnamn
	 * @param address IP-adressen (t ex: 130.236.188.23)
	 * @return Användarens gamla InetAddress, eller null om den inte hade någon tidigare
	 */
	public InetAddress adduser(String userName, String address) {
		try {
			// Testa skapa ett InetAddress-objekt av given adress
			InetAddress adr = InetAddress.getByName(address);
			Sender.broadcast(new OnlineUsersMessage(OnlineOperation.ADD, userName));
			return addUser(userName, adr);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return null;
		}		
	}

	/**
	 * Ta bort en användare från listan och broadcasta att denna nu är offline
	 * 
	 * @param userName Användaren som ska tas bort från listan
	 * @return InetAddressen som var associerad med användaren
	 */

	public InetAddress removeUser(String userName) {
		updatedAssociations.remove(userName);
		Sender.broadcast(new OnlineUsersMessage(OnlineOperation.REMOVE, userName));
		return associations.remove(userName);

	}

	/**
	 * Kolla om en användare är online
,,
,
,
,
,
	 * 
	 * @param username Användaren som ska kollas
	 * @return True om användaren är online
	 */
	public boolean isUserOnline(String username){
		return (getUserAddress(username) != null);
	}

	/**
	 * Kolla om en ip-adress är online
	 * 
	 * @param address Ip-adressen som ska kollas
	 * @return True om ip-adressen är online
	 */
	public boolean isAddressOnline(String address){
		return associations.containsValue(address);
	}

	/**
	 * Hämta en användares InetAddress
	 * 
	 * @param username Användaren vars InetAddress man vill hämta
	 * @return InetAddressen som är associerad med username
	 */
	public InetAddress getUserAddress(String username) {
		if(associations.containsKey(username))
			return associations.get(username);
		return null;
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


	/**
	 * Rapportera om ett mottaget probe-meddelande som checkar så att användaren
	 * fortfarande är online på servern.
	 * @param user Användarens användarnamn
	 * @param address Användarens adress
	 */
	public void confirmedProbeMessage(String user, InetAddress address){
		updatedAssociations.put(user, address);
	}

	
	/**
	 * Returnerar en ArrayList av Strings med alla användarnamn som är online
	 * @return ArrayList<String> med alla användarnamnpå de som är online.
	 */
	public static ArrayList<String> getOnlineUserNames(){
		ArrayList<String> onlineUserNames = new ArrayList<String>();
		onlineUserNames.addAll(associations.keySet());
		return onlineUserNames;
	}
}
