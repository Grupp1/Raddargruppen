package raddar.models;

import raddar.enums.MessageType;
import raddar.enums.SOSType;

/**
 * Ett SOS-meddelande. SOS-meddelanden som skickas till servern broadcastas
 * till alla anslutna klienter. Klienterna som tar emot SOS-meddelanden
 * bör ha implementerat vibration/ljuduppspelning för att användaren ska bli
 * medveten om det akuta SOS-alarmet
 * @author andbo265
 *
 */
public class SOSMessage extends Message {
	
	private SOSType SOSType;
	
	/**
	 * Skapa ett SOS-meddelande
	 * @param msg Ett medföljande meddelande
	 */
	public SOSMessage(String fromUser, SOSType st) {
		this("", fromUser, st);
	}
	
	/**
	 * Skapa ett SOS-meddelande med avsändare
	 * @param msg Medföljande meddelande
	 * @param fromUser Avsändaren
	 */
	public SOSMessage(String msg, String fromUser, SOSType st) {
		this.SOSType = st;
		this.data = msg;
		this.fromUser = fromUser;
		this.type = MessageType.SOS;
	}

	@Override
	public String getFormattedMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	public SOSType getSOSType() {
		return SOSType;
	}

	public void setSOSType(SOSType sOSType) {
		SOSType = sOSType;
	}

}
