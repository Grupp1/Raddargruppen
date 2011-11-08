package raddar.models;

import java.util.Observable;

import raddar.views.MapUI;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;

public class GPSModel extends Observable implements LocationListener {
	
	private int lat = 0;
	private int lon = 0;
	// Ska vara noll senare. Nu är det universitetet.
	private GeoPoint myLocation;
	private LocationManager lm;
	private String towers;
	
	public GPSModel(MapUI mapUI){
		addObserver(mapUI);
		lm = (LocationManager) mapUI.getSystemService(Context.LOCATION_SERVICE);
		Criteria crit = new Criteria();
	
		towers = lm.getBestProvider(crit, false);
		Location location = lm.getLastKnownLocation(towers);
		
		if (location != null){
			lat = (int) (location.getLatitude() * 1E6);
			lon = (int) (location.getLongitude() * 1E6);
			myLocation = new GeoPoint(lat, lon);
			setChanged();
			notifyObservers(myLocation);
		}
		else{
			Toast.makeText(mapUI, "Couldnt get provider", Toast.LENGTH_SHORT).show();
		}
	}

	public void onLocationChanged(Location l) {
		lat = (int) (l.getLatitude() * 1E6);
		lon = (int) (l.getLongitude() * 1E6);
		myLocation = new GeoPoint(lat, lon);
		setChanged();
		notifyObservers(myLocation);
	}

	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	public LocationManager getLocationManager(){
		return lm;
	}
	
	public String getTowers(){
		return towers;
	}
	
}
