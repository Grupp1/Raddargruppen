package raddar.views;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

import raddar.controllers.MapCont;
import raddar.enums.SituationPriority;
import raddar.gruppen.R;
import raddar.models.Fire;
import raddar.models.GPSModel;
import raddar.models.MapObjectList;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
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

public class MapUI extends MapActivity implements Observer {

	private MapView mapView;
	private long start;
	private long stop;
	private MyLocationOverlay compass;
	private MapController controller;
	private int touchedX, touchedY;
	private GeoPoint touchedPoint;
	private Drawable d;
	private List<Overlay> mapOverlays;

	private GPSModel gps;

	private MapCont mapCont;


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

		//MapController mapController = new MapController();
		//MapView mapView = ((MapView)findViewById(R.id.mapview), "0b1qi7XBfQqm8teK24blL1Hhnfhqc9iOFejhYUw");
		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);

		/**
		 *  Lista över alla overlays (lager) som visas på kartan
		 */

		mapOverlays = mapView.getOverlays();

		Touchy t = new Touchy();

		gps = new GPSModel(this);

		Fire fire = new Fire(touchedPoint, "Det brinner här!", SituationPriority.HIGH);

		d = this.getResources().getDrawable(fire.getID());

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
		gps.getLocationManager().removeUpdates(gps);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		compass.enableCompass();
		super.onResume();
		gps.getLocationManager().requestLocationUpdates(gps.getTowers(), 500, 1, gps);
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	class Touchy extends Overlay{
		public boolean onTouchEvent(MotionEvent e, MapView m) {

			//new Runnable() {
			//public void run() {

			//}
			//};


			if(e.getAction() == MotionEvent.ACTION_DOWN){
				start = e.getEventTime();
				touchedX = (int) e.getX();
				touchedY = (int) e.getY();
				touchedPoint = mapView.getProjection().fromPixels(touchedX, touchedY);
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



						Fire f = new Fire(touchedPoint, "Det brinner här!", SituationPriority.HIGH);
						//mapCont.add(f);
						MapObjectList firePlaces = new MapObjectList(d, MapUI.this);
						firePlaces.addOverlay(f);
						mapOverlays.add(firePlaces);


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

	public void update(Observable observable, Object data) {
		if (data instanceof MapObjectList){
			mapOverlays.add((MapObjectList) data);
		}
	}

}
