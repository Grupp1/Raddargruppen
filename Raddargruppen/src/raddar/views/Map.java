package raddar.views;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import raddar.gruppen.R;
import android.app.AlertDialog;
import android.content.DialogInterface;
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

public class Map extends MapActivity {

	private MapView mapView;
	private long start;
	private long stop;
	private MyLocationOverlay compass;
	private MapController controller;
	private int x, y;
	GeoPoint touchedPoint;

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
		this.setSatelliteView(true);


		/**
		 *  Lista över alla overlays (lager) som visas på kartan
		 */
		List<Overlay> mapOverlays = mapView.getOverlays();
		
		Touchy t = new Touchy();
		

		//Drawable drawable;
//		drawable = this.getResources().getDrawable(R.drawable.magnus);
//		MapObjectList magnusList= new MapObjectList(drawable);

		//drawable = this.getResources().getDrawable(R.drawable.niklas);
		//MapObjectList niklasList = new MapObjectList(drawable);

//		drawable = this.getResources().getDrawable(R.drawable.resource);
//		MapObjectList resourceList = new MapObjectList(drawable);


		// Skapar en nytt object på kartan
		//MapObject niklas = new MapObject(new GeoPoint(52395730, 65573080), "Niklas", "Hallo", "niklas");

		// Lägger till objekten i kategorierna
		//niklasList.addOverlay(niklas);

		// lägger på objekt "över" varandra
		//mapOverlays.add(niklasList);
		mapOverlays.add(t);
		compass = new MyLocationOverlay(Map.this, mapView);
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
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		compass.enableCompass();
		super.onResume();
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setSatelliteView(boolean on){
		mapView.setSatellite(on);
	}

	public void setTrafficView(boolean on){
		mapView.setTraffic(on);
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
				System.out.println("hajklsfhaklsdh");
				AlertDialog alert = new AlertDialog.Builder(Map.this).create();
				alert.setTitle("Hej");
				alert.setMessage("Välj en knapp");
				alert.setButton("place a pin", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

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
				alert.setButton3("hejsan", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

					}
				});
				alert.show();
				return true;
			}
			return false;
		}
	}

}
