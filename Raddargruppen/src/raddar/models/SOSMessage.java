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
	
	private int lat, lon;
	private ResourceStatus status;
	private SOSType SOSType;
	private boolean SOS;
	
	/**
	 * Klassen används för att skicka SOS-meddelanden mellan klienter
	 * 
	 * @param fromUser Meddelandets avsändare
	 * @param lat Objektets latitud
	 * @param lon Objektets longitud
	 * @param title Objektets titel, kan hämtas via getSubject()
	 * @param snippet Meddelandets text(beskrivning), hämtas via getData()
	 * @param st SOSType, ALARM eller CANCEL
	 * @param status Objektets status
	 * @param SOS true om objektet har anropat SOS
	 */
	public SOSMessage(String fromUser, int lat, int lon, String title, String snippet,
			SOSType st, ResourceStatus status, boolean SOS){
		this.fromUser = fromUser;
		this.lat = lat;
		this.lon = lon;
		this.subject = title;
		this.data = snippet;
		this.SOSType = st;
		this.status = status;
		this.SOS = SOS;
		this.type = MessageType.SOS;
	}
	
	public boolean isSOS(){
		return SOS;
	}
	
	public GeoPoint getPoint(){
		return new GeoPoint(lat, lon);
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
}
