package raddar.models;

import raddar.enums.MessageType;

/**
 * Ett SOS-meddelande. SOS-meddelanden som skickas till servern broadcastas
 * till alla anslutna klienter. Klienterna som tar emot SOS-meddelanden
 * b�r ha implementerat vibration/ljuduppspelning f�r att anv�ndaren ska bli
 * medveten om det akuta SOS-alarmet
 * @author andbo265
 *
 */
public class SOSMessage extends Message {
	
	/**
	 * Skapa ett SOS-meddelande
	 * @param msg Ett medf�ljande meddelande
	 */
	public SOSMessage(String msg) {
		this(msg, "");
	}
	
	/**
	 * Skapa ett SOS-meddelande med avs�ndare
	 * @param msg Medf�ljande meddelande
	 * @param fromUser Avs�ndaren
	 */
	public SOSMessage(String msg, String fromUser) {
		this.data = msg;
		this.fromUser = fromUser;
		this.type = MessageType.SOS;
	}
	

	@Override
	public String getFormattedMessage() {
		// TODO Auto-generated method stub
		return null;
	}

}
