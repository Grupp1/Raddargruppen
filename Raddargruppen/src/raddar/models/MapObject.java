package raddar.models;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import raddar.controllers.SessionController;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

/**
 * 
 * @author nikla729
 *
 */
public class MapObject extends OverlayItem {

	private GeoPoint point;
	private String title, snippet, adress, description, addedBy, date, changedBy, changedDate;
	protected String id;
	private int icon;
	private ID idGen;

	/**
	 * Super-klassen för ett objekt på kartan
	 * 
	 * @param point Koordinater
	 * @param title Titel
	 * @param snippet Beskrivning
	 * @param icon Ikon
	 */
	public MapObject(GeoPoint point, String title, String snippet, int icon) {
		super(point, title, snippet);
		this.point = point;
		this.title = title;
		this.snippet = snippet;
		this.icon = icon;
		this.addedBy = SessionController.getUser();
		this.date = new SimpleDateFormat("yyyy:MM:dd 'kl' HH:mm:ss").format(new Date());


		this.changedBy = "Ej ändrad";
		this.changedDate = "Ej ändrad";


		if(point!= null && addedBy!= null&& date != null){
			idGen = new ID(addedBy,point.getLatitudeE6(),point.getLongitudeE6(), date);
			id = idGen.generateID();
		}

	}

	public String getChangedBy() {
		return changedBy;
	}

	public String getChangedDate() {
		return changedDate;
	}

	protected void changeLatestUser(){
		changedBy = SessionController.getUser();
		changedDate = new SimpleDateFormat("yyyy:MM:dd 'kl' HH:mm:ss").format(new Date());
	}

	/**
	 * 
	 * @return Skaparens användarnamn
	 */
	public String getAddedBy(){
		return addedBy;
	}

	/**
	 * 
	 * @return Tiden och datumet då objektet skapades
	 */
	public String getDate(){
		return date;
	}

	/**
	 * 
	 * @param point Objektets koordinater
	 */
	public void setPoint(GeoPoint point) {
		this.point = point;
	}

	/**
	 * @return Objektets titel
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * 
	 * @param title Objektets titel
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * 
	 * @param snippet Objektets beskrivning
	 */
	public void setSnippet(String snippet) {
		this.snippet = snippet;
		changeLatestUser();
	}

	/**
	 * @return Objektets beskrivning
	 */
	public String getSnippet(){
		return snippet;
	}

	/**
	 * 
	 * @return Objektets specifika id
	 */
	public String getId() {
		return id;
	}

	/**
	 * 
	 * @return Objektets ikon
	 */
	public int getIcon() {
		return icon;
	}

	/**
	 * 
	 * @param icon Objektets ikon
	 */
	public void setIcon(int icon) {
		this.icon = icon;
	}

	/**
	 * 
	 * @return Objektets adress
	 */
	public String getAdress() {
		return adress;
	}


	/**
	 * 
	 * @param adress Objektets adress
	 */
	public void setAdress(String adress) {
		this.adress = adress;
	}

	/**
	 * Returnerar en sträng som visar all relevant information för det specifika objektet
	 * @return Objektets relevanta information för användaren
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * 
	 * @param description En sträng som samlar objektets relevanta information
	 */
	public void setDescription(String description){
		this.description = description;
	}

	/**
	 * Anropar updateAdress och updateDescription
	 * @param geocoder Kartans geocoder
	 */
	public void updateData(Geocoder geocoder){
		updateAdress(geocoder);
		updateDescription();
	}	

	public GeoPoint getPoint() {
		return point;
	}

	/**
	 * Tar fram objektets geografiska adress från dess koordinater
	 * @param geocoder Kartans geocoder
	 */
	public void updateAdress(Geocoder geocoder){
		String display ="Kunde inte hämta adress";
		try{
			List<Address> address = geocoder.getFromLocation(point.getLatitudeE6() / 1E6, point.getLongitudeE6() / 1E6, 1);
			display = "";
			if(address.size() > 0){
				for(int i = 0; i<address.get(0).getMaxAddressLineIndex(); i++){
					display += address.get(0).getAddressLine(i) + "\n";
				}
			}
		} catch (IOException e) {
			//e.printStackTrace();
			Log.d("Geocoder", "Service not avalible");
		}finally{

		}
		setAdress(display);
	}

	/**
	 * Uppdaterar strängen description med objektets variabler
	 */
	public void updateDescription(){
		setDescription("Beskrivning: " + "\n" +getSnippet()+"\n\nAdress: "+getAdress()+
				"Koordinater:" + "\n" +getPoint().getLatitudeE6()/1E6+", "+getPoint().getLongitudeE6()/1E6 + "\n\nSkapad: " + 
				getDate() + "\nSkapad av: " + getAddedBy() + "\nSenast ändrad: " + getChangedDate() + "\nSenast ändrad av: " +
				getChangedBy() + "\n" + "ID: " + getId()+ "\n");
	}

}

