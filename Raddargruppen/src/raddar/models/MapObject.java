package raddar.models;

import java.io.IOException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.List;

import raddar.views.MainView;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class MapObject extends OverlayItem {

	private GeoPoint point;
	private String title, snippet, id, adress, description, addedBy, date;
	private int icon;
	
	
	/*
	 * MapObject är ett object som kan placeras ut på kartan
	 */

	public MapObject(GeoPoint point, String title, String snippet, int icon, String id) {
		super(point, title, snippet);
		this.point = point;
		this.title = title;
		this.snippet = snippet;
		this.id = id;
		this.icon = icon;
		this.addedBy = MainView.controller.getUser();
		this.date = new SimpleDateFormat("yyyy:MM:dd 'kl' HH:mm:ss").format(new Date());
	}
	
	public String getAddedBy(){
		return addedBy;
	}
	
	public String getDate(){
		return date;
	}
	
	public GeoPoint getPoint() {
		return point;
	}

	public void setPoint(GeoPoint point) {
		this.point = point;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSnippet() {
		return snippet;
	}

	public void setSnippet(String snippet) {
		this.snippet = snippet;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public int getIcon() {
		return icon;
	}

	public void setIcon(int icon) {
		this.icon = icon;
	}
	
	public String getAdress() {
		if(adress == null){
			return "";
		}
		return adress;
	}

	public void setAdress(String adress) {
		this.adress = adress;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description){
		this.description = description;
	}
	
	public void updateData(Geocoder geocoder){
		updateAdress(geocoder);
		updateDescription();
	}
	
	/*
	 * Uppdaterar adressen när den ändras
	 */
	
	public void updateAdress(Geocoder geocoder){
		String display ="";
		try{
			List<Address> address = geocoder.getFromLocation(point.getLatitudeE6() / 1E6, point.getLongitudeE6() / 1E6, 1);
			if(address.size() > 0){
				for(int i = 0; i<address.get(0).getMaxAddressLineIndex(); i++){
					display += address.get(0).getAddressLine(i) + "\n";
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			
		}
		if (display.equals("")){
			display = "Kunde inte hämta adress\n";
		}
		setAdress(display);
	}
	
	public void updateDescription(){
		setDescription("Beskrivning: "+getSnippet()+"\nAdress: "+getAdress()+
				  "Koordinater: "+getPoint().getLatitudeE6()/1E6+", "+getPoint().getLongitudeE6()/1E6 + "\nSkapad: " + 
				getDate() + "\nSkapad av: " + getAddedBy());
	}
	
}
