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
		long delay = 45000;
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask(){
			@Override
			public void run() {
				// Ersï¿½tter listan med den uppdaterade frï¿½n fï¿½rra probe-sessionen
				associations.clear();
				associations.putAll(updatedAssociations);
				updatedAssociations.clear();
				
				// Skickar ut nya probes
				if(onlineUsers()>0){
					Set<String> keys = associations.keySet();
					//System.out.println("Användare online: " +keys.size());
					for (String key: keys){
						//System.out.println("Online: "+key+", "+associations.get(key));
						NotificationMessage m = new NotificationMessage("server", null);
						m.setType(MessageType.PROBE);
						new Sender(m,associations.get(key));
					}
				}
			}
		}, delay, delay);
	}

	/**
	 * Lï¿½gg till en anvï¿½ndare och tillsammans med dennes InetAddress
	 * och broadcasta att denna nu ï¿½r online.
	 * 
	 * @param username Anvï¿½ndarens anvï¿½ndarnamn
	 * @param address Anvï¿½ndarens InetAddress
	 * @return Anvï¿½ndarens gamla InetAddress, eller null om den inte hade nï¿½gon tidigare
	 */

	public InetAddress addUser(String userName, InetAddress address) {
		updatedAssociations.put(userName, address);
		Sender.broadcast(new OnlineUsersMessage(OnlineOperation.ADD, userName));
		return associations.put(userName, address);

	}

	/**
	 * Lï¿½gg till en anvï¿½ndare och tillsammans med dennes IP-adress och
	 * broadcasta att denna nu ï¿½r online
	 * 
	 * @param username Anvï¿½ndarens anvï¿½ndarnamn
	 * @param address IP-adressen (t ex: 130.236.188.23)
	 * @return Anvï¿½ndarens gamla InetAddress, eller null om den inte hade nï¿½gon tidigare
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
	 * Ta bort en anvï¿½ndare frï¿½n listan och broadcasta att denna nu ï¿½r offline
	 * 
	 * @param userName Anvï¿½ndaren som ska tas bort frï¿½n listan
	 * @return InetAddressen som var associerad med anvï¿½ndaren
	 */

	public InetAddress removeUser(String userName) {
		updatedAssociations.remove(userName);
		Sender.broadcast(new OnlineUsersMessage(OnlineOperation.REMOVE, userName));
		return associations.remove(userName);

	}

	/**
	 * Kolla om en anvï¿½ndare ï¿½r online
,,
,
,
,
,
	 * 
	 * @param username Anvï¿½ndaren som ska kollas
	 * @return True om anvï¿½ndaren ï¿½r online
	 */
	public boolean isUserOnline(String username){
		return (getUserAddress(username) != null);
	}

	/**
	 * Kolla om en ip-adress ï¿½r online
	 * 
	 * @param address Ip-adressen som ska kollas
	 * @return True om ip-adressen ï¿½r online
	 */
	public boolean isAddressOnline(String address){
		return associations.containsValue(address);
	}

	/**
	 * Hï¿½mta en anvï¿½ndares InetAddress
	 * 
	 * @param username Anvï¿½ndaren vars InetAddress man vill hï¿½mta
	 * @return InetAddressen som ï¿½r associerad med username
	 */
	public InetAddress getUserAddress(String username) {
		if(associations.containsKey(username))
			return associations.get(username);
		return null;
	}

	/**
	 * Kolla hur mï¿½nga anvï¿½ndare som ï¿½r online och associerade
	 * @return Antalet anvï¿½ndare som ï¿½r online
	 */
	public int onlineUsers() {
		return associations.size();
	}

	/**
	 * Hï¿½mta listan av online anvï¿½ndare
	 * @return HashMapen med alla online anvï¿½ndare
	 */
	public HashMap<String, InetAddress> getAllAssociations() {
		return associations;
	}


	/**
	 * Rapportera om ett mottaget probe-meddelande som checkar sï¿½ att anvï¿½ndaren
	 * fortfarande ï¿½r online pï¿½ servern.
	 * @param user Anvï¿½ndarens anvï¿½ndarnamn
	 * @param address Anvï¿½ndarens adress
	 */
	public void confirmedProbeMessage(String user, InetAddress address){
		updatedAssociations.put(user, address);
	}

	
	/**
	 * Returnerar en ArrayList av Strings med alla anvï¿½ndarnamn som ï¿½r online
	 * @return ArrayList<String> med alla anvï¿½ndarnamnpï¿½ de som ï¿½r online.
	 */
	public static ArrayList<String> getOnlineUserNames(){
		ArrayList<String> onlineUserNames = new ArrayList<String>();
		onlineUserNames.addAll(associations.keySet());
		return onlineUserNames;
	}
}
