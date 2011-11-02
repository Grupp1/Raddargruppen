package raddar.views;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import raddar.enums.ResourceStatus;
import raddar.enums.SituationPriority;
import raddar.gruppen.R;
import raddar.models.MapObjectList;
import raddar.models.Resource;
import raddar.models.Situation;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;

public class MapUI extends MapActivity implements LocationListener {

	private MapView mapView;
	private long start;
	private long stop;
	private MyLocationOverlay compass;
	private MapController controller;
	private int x, y;
	int lat = 58395730;
	int lon = 15573080;
	GeoPoint touchedPoint;
	GeoPoint myLocation;
	Drawable d;
	List<Overlay> mapOverlays;
	LocationManager lm;
	String towers;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.maps);

		Button close;
		close = (Button)this.findViewById(R.id.button_close);
		close.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});


		//MapView mapView = ((MapView)findViewById(R.id.mapview), "0b1qi7XBfQqm8teK24blL1Hhnfhqc9iOFejhYUw");
		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);


		/**
		 *  Lista över alla overlays (lager) som visas på kartan
		 */
		mapOverlays = mapView.getOverlays();

		Touchy t = new Touchy();


		// Vår position
		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Criteria crit = new Criteria();

		towers = lm.getBestProvider(crit, false);
		Location location = lm.getLastKnownLocation(towers);

		d = getResources().getDrawable(R.drawable.magnus);
		
		if (location != null){
			lat = (int) (location.getLatitude() * 1E6);
			lon = (int) (location.getLongitude() * 1E6);

			myLocation = new GeoPoint(lat, lon);
			Resource magnus = new Resource(myLocation, "Magnus", "Här är jag!", "00000", ResourceStatus.FREE);
			MapObjectList gay = new MapObjectList(d, MapUI.this);
			gay.addOverlay(magnus);
			mapOverlays.add(gay);
		}
		else{
			Toast.makeText(MapUI.this, "Couldnt get provider", Toast.LENGTH_SHORT).show();
		}

		d = getResources().getDrawable(R.drawable.fire);

		// Skapar en nytt object på kartan
		//MapObject niklas = new MapObject(new GeoPoint(52395730, 65573080), "Niklas", "Hallo", "niklas");

		// Lägger till objekten i kategorierna
		//niklasList.addOverlay(niklas);

		// lägger på objekt "över" varandra
		//mapOverlays.add(niklasList);
		mapOverlays.add(t);
		compass = new MyLocationOverlay(MapUI.this, mapView);
		mapOverlays.add(compass);
		controller = mapView.getController();
		GeoPoint point = new GeoPoint(58395730, 15573080);
		controller.animateTo(point);
		controller.setZoom(15);



	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		compass.disableCompass();
		super.onPause();
		lm.removeUpdates(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		compass.enableCompass();
		super.onResume();
		lm.requestLocationUpdates(towers, 500, 1, this);
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	class Touchy extends Overlay{
		public boolean onTouchEvent(MotionEvent e, MapView m){
			if(e.getAction() == MotionEvent.ACTION_DOWN){
				start = e.getEventTime();
				x = (int) e.getX();
				y = (int) e.getY();
				touchedPoint = mapView.getProjection().fromPixels(x, y);
			}
			if(e.getAction() == MotionEvent.ACTION_UP){
				stop = e.getEventTime();
			}
			if(stop - start > 1500){
				AlertDialog alert = new AlertDialog.Builder(MapUI.this).create();
				alert.setTitle("Hej");
				alert.setMessage("Välj en knapp");



				alert.setButton("Placera", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Situation situation = new Situation(touchedPoint, "Eld", "Det brinner här!", "000000", SituationPriority.HIGH);
						MapObjectList fire = new MapObjectList(d, MapUI.this);
						fire.addOverlay(situation);
						mapOverlays.add(fire);
					}
				});






				alert.setButton2("get adress", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Geocoder geocoder = new Geocoder(getBaseContext(), Locale.getDefault());
						try{
							List<Address> address = geocoder.getFromLocation(touchedPoint.getLatitudeE6() / 1E6, touchedPoint.getLongitudeE6() / 1E6, 1);
							if(address.size() > 0){
								String display ="";
								for(int i = 0; i<address.get(0).getMaxAddressLineIndex(); i++){
									display += address.get(0).getAddressLine(i) + "\n";
								}
								Toast t = Toast.makeText(getBaseContext(), display, Toast.LENGTH_LONG);
								t.show();
							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}finally{

						}
					}
				});




				alert.setButton3("Toggle View", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if(mapView.isSatellite()){
							mapView.setSatellite(false);
						}
						else{
							mapView.setSatellite(true);
						}
					}
				});
				alert.show();
				return true;
			}
			return false;
		}
	}

	protected boolean isSatellite() {
		// TODO Auto-generated method stub
		return false;
	}



/**
 * -----------------------
 * LocationListener metoder
 * -----------------------
 */

	public void onLocationChanged(Location l) {
		// TODO Auto-generated method stub
		lat = (int) (l.getLatitude() * 1E6);
		lon = (int) (l.getLongitude() * 1E6);
		myLocation = new GeoPoint(lat, lon);
		Resource magnus = new Resource(myLocation, "Magnus", "Här är jag!", "00000", ResourceStatus.FREE);
		MapObjectList gay = new MapObjectList(d, MapUI.this);
		gay.addOverlay(magnus);
		mapOverlays.add(gay);
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

}
