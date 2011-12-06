package raddar.models;

import raddar.enums.MessageType;
import raddar.enums.ResourceStatus;
import raddar.enums.SOSType;
import raddar.views.MainView;

import android.util.Log;

import com.google.android.maps.GeoPoint;

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
	private int lat, lon;
	private ResourceStatus status;
	private boolean isSOS;


	/**
	 * Skapa ett SOS-meddelande med avsändare
	 * @param msg Meddelandet
	 * @param you Användaren
	 * @param st SOS-typen, alarm eller cancel
	 */
	public SOSMessage(String msg, String fromUser, int lat, int lon, SOSType st, ResourceStatus status, boolean isSOS){
		this.data = msg;
		this.fromUser = fromUser;
		this.type = MessageType.SOS;
		this.SOSType = st;
		this.lat = lat;
		this.lon = lon;
		this.isSOS = isSOS;
		this.status = status;
	}
	
	public GeoPoint getPoint(){
		return new GeoPoint(lat, lon);
	}
	
	public boolean isSOS(){
		return isSOS;
	}

	public ResourceStatus getStatus() {
		return status;
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
