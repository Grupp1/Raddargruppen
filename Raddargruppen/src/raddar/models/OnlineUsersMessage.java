package raddar.models;

import raddar.enums.MessageType;
import raddar.enums.OnlineOperation;

public class OnlineUsersMessage extends Message  {

	private OnlineOperation onlineOperation;
	private String userName;
	
	/**
	 * Skapar ett OnlineUserMessage som inneh�ller infomration om an kontakts onlinestatus. Meddelandet talar om f�r klienten
	 * omd en ska l�gga till eller ta bort en kontakt ur listan.
	 * @param onlineOperation Enum av typen OnlineOperation. ADD l�gger till en anv�ndare i onlinelistan och REMOVE tar bort anv�ndare.
	 * @param userName Anv�ndaren som ska behandlas i onlinelistan
	 */
	public OnlineUsersMessage(OnlineOperation onlineOperation, String userName){
		this.onlineOperation = onlineOperation;
		this.userName = userName;
		this.type = MessageType.ONLINE_USERS;
	}


	@Override
	public String getFormattedMessage() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Kollar vilken OnlineOperation meddelandet inneh�ller
	 * @return Returnerar vilken operation som ska utf�ras
	 */
	public OnlineOperation getOnlineOperation(){
		return onlineOperation;
	}
	
	/**
	 * Kollar vilkar anv�ndarnamn som ska behandlas i onlinedatabasen
	 * @return anv�ndarnamnet
	 */
	public String getUserName(){
		return userName;
	}
}
