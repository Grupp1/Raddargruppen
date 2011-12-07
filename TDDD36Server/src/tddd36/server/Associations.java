package tddd36.server;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import raddar.enums.MessageType;
import raddar.enums.OnlineOperation;
import raddar.models.NotificationMessage;
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
				// Ers�tter listan med den uppdaterade fr�n f�rra probe-sessionen
				associations.clear();
				associations.putAll(updatedAssociations);
				updatedAssociations.clear();
				
				// Skickar ut nya probes
				if(onlineUsers()>0){
					Set<String> keys = associations.keySet();
					for (String key: keys){
						NotificationMessage m = new NotificationMessage("server", null);
						m.setType(MessageType.PROBE);
						new Sender(m,associations.get(key));
					}
				}
			}
		}, delay, delay);
	}

	/**
	 * L�gg till en anv�ndare och tillsammans med dennes InetAddress
	 * och broadcasta att denna nu �r online.
	 * 
	 * @param username Anv�ndarens anv�ndarnamn
	 * @param address Anv�ndarens InetAddress
	 * @return Anv�ndarens gamla InetAddress, eller null om den inte hade n�gon tidigare
	 */

	public InetAddress addUser(String userName, InetAddress address) {
		updatedAssociations.put(userName, address);
		Sender.broadcast(new OnlineUsersMessage(OnlineOperation.ADD, userName));
		return associations.put(userName, address);

	}

	/**
	 * L�gg till en anv�ndare och tillsammans med dennes IP-adress och
	 * broadcasta att denna nu �r online
	 * 
	 * @param username Anv�ndarens anv�ndarnamn
	 * @param address IP-adressen (t ex: 130.236.188.23)
	 * @return Anv�ndarens gamla InetAddress, eller null om den inte hade n�gon tidigare
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
	 * Ta bort en anv�ndare fr�n listan och broadcasta att denna nu �r offline
	 * 
	 * @param userName Anv�ndaren som ska tas bort fr�n listan
	 * @return InetAddressen som var associerad med anv�ndaren
	 */

	public InetAddress removeUser(String userName) {
		updatedAssociations.remove(userName);
		Sender.broadcast(new OnlineUsersMessage(OnlineOperation.REMOVE, userName));
		return associations.remove(userName);

	}

	/**
	 * Kolla om en anv�ndare �r online
,,
,
,
,
,
	 * 
	 * @param username Anv�ndaren som ska kollas
	 * @return True om anv�ndaren �r online
	 */
	public boolean isUserOnline(String username){
		return (getUserAddress(username) != null);
	}

	/**
	 * Kolla om en ip-adress �r online
	 * 
	 * @param address Ip-adressen som ska kollas
	 * @return True om ip-adressen �r online
	 */
	public boolean isAddressOnline(String address){
		return associations.containsValue(address);
	}

	/**
	 * H�mta en anv�ndares InetAddress
	 * 
	 * @param username Anv�ndaren vars InetAddress man vill h�mta
	 * @return InetAddressen som �r associerad med username
	 */
	public InetAddress getUserAddress(String username) {
		if(associations.containsKey(username))
			return associations.get(username);
		return null;
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


	/**
	 * Rapportera om ett mottaget probe-meddelande som checkar s� att anv�ndaren
	 * fortfarande �r online p� servern.
	 * @param user Anv�ndarens anv�ndarnamn
	 * @param address Anv�ndarens adress
	 */
	public void confirmedProbeMessage(String user, InetAddress address){
		updatedAssociations.put(user, address);
	}

	
	/**
	 * Returnerar en ArrayList av Strings med alla anv�ndarnamn som �r online
	 * @return ArrayList<String> med alla anv�ndarnamnp� de som �r online.
	 */
	public static ArrayList<String> getOnlineUserNames(){
		ArrayList<String> onlineUserNames = new ArrayList<String>();
		onlineUserNames.addAll(associations.keySet());
		return onlineUserNames;
	}
}
