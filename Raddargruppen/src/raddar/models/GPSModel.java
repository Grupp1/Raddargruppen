package raddar.models;

import raddar.enums.ResourceStatus;
import raddar.gruppen.R;
import raddar.views.MapUI;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;
import raddar.gruppen.R;

public class GPSModel implements LocationListener {
	
	private int lat = 58395730;
	private int lon = 15573080;
	// Ska vara noll senare. Nu är det universitetet.
	private GeoPoint myLocation;
	private LocationManager lm;
	private String towers;
	
	public GPSModel(MapActivity map){
		/**
		 * MÅSTE LÖSAS, GPSEN VET OM KARTAN?
		 */
		lm = (LocationManager) map.getSystemService(Context.LOCATION_SERVICE);
		Criteria crit = new Criteria();
	
		towers = lm.getBestProvider(crit, false);
		Location location = lm.getLastKnownLocation(towers);

		//d = getResources().getDrawable(R.drawable.magnus);
		
		if (location != null){
			lat = (int) (location.getLatitude() * 1E6);
			lon = (int) (location.getLongitude() * 1E6);

			myLocation = new GeoPoint(lat, lon);
			Resource magnus = new Resource(myLocation, "Magnus", "Här är jag!", R.drawable.magnus, "00000", ResourceStatus.FREE);
			//MapObjectList gay = new MapObjectList(d, MapUI.this);
			//gay.addOverlay(magnus);
			//mapOverlays.add(gay);
		}
		else{
			//Toast.makeText(MapUI.this, "Couldnt get provider", Toast.LENGTH_SHORT).show();
		}
	}

	public void onLocationChanged(Location l) {
		lat = (int) (l.getLatitude() * 1E6);
		lon = (int) (l.getLongitude() * 1E6);
		myLocation = new GeoPoint(lat, lon);
		Resource magnus = new Resource(myLocation, "Magnus", "Här är jag!", R.drawable.magnus, "00000", ResourceStatus.FREE);
		//MapObjectList gay = new MapObjectList(d, MapUI.this);
		//gay.addOverlay(magnus);
		//mapOverlays.add(gay);
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
