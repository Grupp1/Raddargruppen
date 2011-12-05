package raddar.models;

import raddar.enums.MessageType;
import raddar.enums.SOSType;
import raddar.views.MainView;

import android.util.Log;

import com.google.android.maps.GeoPoint;

/**
 * Ett SOS-meddelande. SOS-meddelanden som skickas till servern broadcastas
 * till alla anslutna klienter. Klienterna som tar emot SOS-meddelanden
 * b�r ha implementerat vibration/ljuduppspelning f�r att anv�ndaren ska bli
 * medveten om det akuta SOS-alarmet
 * @author andbo265
 *
 */
public class SOSMessage extends Message {
	
	private SOSType SOSType;
	private int longitude;
	private int latitude;

	/**
	 * Skapa ett SOS-meddelande med avs�ndare
	 * @param msg Medf�ljande meddelande
	 * @param fromUser Avs�ndaren
	 */
	public SOSMessage(String msg, String fromUser, SOSType st,int longitude,int latitude) {
		this.SOSType = st;
		this.data = msg;
		this.fromUser = fromUser;
		this.type = MessageType.SOS;
		this.latitude = latitude;
		this.longitude = longitude;
		Log.e("SEND SOS", longitude+" ");
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

	public GeoPoint getPoint() {
		return new GeoPoint(latitude, longitude);
	}
}
