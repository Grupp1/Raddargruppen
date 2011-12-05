package raddar.models;

import raddar.enums.MessageType;
import raddar.enums.OnlineOperation;

public class OnlineUsersMessage extends Message  {

	private OnlineOperation onlineOperation;
	private String userName;
	
	/**
	 * Skapar ett OnlineUserMessage som innehåller infomration om an kontakts onlinestatus. Meddelandet talar om för klienten
	 * omd en ska lägga till eller ta bort en kontakt ur listan.
	 * @param onlineOperation Enum av typen OnlineOperation. ADD lägger till en användare i onlinelistan och REMOVE tar bort användare.
	 * @param userName Användaren som ska behandlas i onlinelistan
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
	 * Kollar vilken OnlineOperation meddelandet innehåller
	 * @return Returnerar vilken operation som ska utföras
	 */
	public OnlineOperation getOnlineOperation(){
		return onlineOperation;
	}
	
	/**
	 * Kollar vilkar användarnamn som ska behandlas i onlinedatabasen
	 * @return användarnamnet
	 */
	public String getUserName(){
		return userName;
	}
}
