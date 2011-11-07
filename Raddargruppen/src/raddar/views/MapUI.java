package raddar.views;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

import raddar.controllers.MapCont;
import raddar.enums.ResourceStatus;
import raddar.enums.SituationPriority;
import raddar.gruppen.R;
import raddar.models.Fire;
import raddar.models.FireTruck;
import raddar.models.GPSModel;
import raddar.models.MapObject;
import raddar.models.MapObjectList;
import raddar.views.Map.Touchy;
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
	private GeoPoint touchedPoint, point;
	private Drawable d;
	private List<Overlay> mapOverlays;

	private GPSModel gps;

	private MapCont mapCont;
	private MapCont mapCont1;


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
		 *  Lista �ver alla overlays (lager) som visas p� kartan
		 */

		mapOverlays = mapView.getOverlays();



		compass = new MyLocationOverlay(MapUI.this, mapView);
		mapOverlays.add(compass);
		controller = mapView.getController();


		point = new GeoPoint(58395730, 15573080);
		controller.animateTo(point);
		controller.setZoom(15);



		mapCont = new MapCont(MapUI.this, new Fire(point, "Det brinner h�r!", "000000", SituationPriority.HIGH));
		mapCont1 = new MapCont(MapUI.this, new FireTruck(point, "Vi �r p� v�g", "00000", ResourceStatus.BUSY));
		//mapCont.add(new Fire(point, "Det brinner h�r!", "000000", SituationPriority.HIGH));



		//d = getResources().getDrawable(fire.getIcon());

	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub

		Touchy t = new Touchy();
		mapOverlays.add(t);
		gps = new GPSModel(this);

		super.onStart();


	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		compass.enableCompass();
		super.onResume();
		gps.getLocationManager().requestLocationUpdates(gps.getTowers(), 500, 1, gps);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		compass.disableCompass();
		super.onPause();
		gps.getLocationManager().removeUpdates(gps);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
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
				alert.setTitle("Karta");
				alert.setMessage("Tryck p� en knapp");



				alert.setButton("Brand", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						//						Fire f = new Fire(touchedPoint, "Det brinner h�r!", "00000", SituationPriority.HIGH);
						//						d = getResources().getDrawable(f.getIcon());
						//						MapObjectList firePlaces = new MapObjectList(d, MapUI.this);
						//						firePlaces.addOverlay(f);
						//						mapOverlays.add(firePlaces);

						mapCont.add(new Fire(touchedPoint, "Det brinner h�r!", "00000", SituationPriority.HIGH));

					}
				});



				//					public void onClick(DialogInterface dialog, int which) {	
				//						AlertDialog choose = new AlertDialog.Builder(MapUI.this).create();
				//						choose.setTitle("V�lj objekt");
				//						choose.setMessage("Tryck igen");
				//
				//						choose.setButton("Brand", new DialogInterface.OnClickListener() {
				//							public void onClick(DialogInterface dialog, int which) {
				//								Fire f = new Fire(touchedPoint, "Det brinner h�r!", "00000", SituationPriority.HIGH);
				//								d = getResources().getDrawable(f.getIcon());
				//								MapObjectList firePlaces = new MapObjectList(d, MapUI.this);
				//								firePlaces.addOverlay(f);
				//								mapOverlays.add(firePlaces);
				//							}
				//						});
				//
				//						choose.setButton2("Brandbil", new DialogInterface.OnClickListener() {
				//							public void onClick(DialogInterface dialog, int which) {
				//								FireTruck f = new FireTruck(touchedPoint, "H�r kommer hj�lpen!", "00000", ResourceStatus.FREE);
				//								d = getResources().getDrawable(f.getIcon());
				//								MapObjectList fireTrucks = new MapObjectList(d, MapUI.this);
				//								fireTrucks.addOverlay(f);
				//								mapOverlays.add(fireTrucks);
				//							}
				//						});
				//
				//
				//					}


				alert.setButton2("Brandbil", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						FireTruck f = new FireTruck(touchedPoint, "H�r kommer hj�lpen!", "00000", ResourceStatus.FREE);
						d = getResources().getDrawable(f.getIcon());
						MapObjectList fireTrucks = new MapObjectList(d, MapUI.this);
						fireTrucks.addOverlay(f);
						mapOverlays.add(fireTrucks);
					}
				});


				// SYNS INTE I MENYN, F�R ENDAST PLATS TRE ALTERNATIV
				alert.setButton3("get adress", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int wich){
						Fire f = new Fire(touchedPoint, "Det brinner h�r!", "00000", SituationPriority.HIGH);
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
		// TODO Auto-generated method stub


		if (data instanceof MapObjectList){
			//d = getResources().getDrawable(((MapObjectList) data).getIcon());
			mapOverlays.add((MapObjectList) data);
		}

	}

}
