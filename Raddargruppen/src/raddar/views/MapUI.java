package raddar.views;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import raddar.controllers.MapCont;
import raddar.enums.ResourceStatus;
import raddar.enums.SituationPriority;
import raddar.gruppen.R;
import raddar.models.Fire;
import raddar.models.FireTruck;
import raddar.models.GPSModel;
import raddar.models.MapObjectList;
import raddar.models.Resource;
import raddar.models.Situation;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
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
	private GeoPoint touchedPoint, liu, myLocation;
	private Resource you;
	private List<Overlay> mapOverlays;
	private GPSModel gps;
	private MapCont mapCont;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.maps);
		Button close;
		close = (Button) this.findViewById(R.id.button_close);
		close.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});

		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		mapView.setSatellite(true);

		/**
		 *  Lista över alla overlays (lager) som visas på kartan
		 */
		mapOverlays = mapView.getOverlays();

		compass = new MyLocationOverlay(MapUI.this, mapView);
		mapOverlays.add(compass);
		controller = mapView.getController();

		myLocation = new GeoPoint(0,0);
		liu = new GeoPoint(58395730, 15573080);
		//controller.animateTo(liu);
		controller.setZoom(15);

		gps = new GPSModel(this);
		you = new Resource(myLocation, "Min position", "Här är jag", R.drawable.niklas, "000000", ResourceStatus.FREE);
		mapCont = new MapCont(MapUI.this, you);
		//mapCont.add(new FireTruck(new GeoPoint(58395769, 15573489), "Vi är på väg", "00000", ResourceStatus.BUSY));

		Touchy t = new Touchy(this);
		mapOverlays.add(t);

	} 

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onResume() {
		compass.enableCompass();
		super.onResume();
		gps.getLocationManager().requestLocationUpdates(gps.getTowers(), 500, 1, gps);
	}

	@Override
	protected void onPause() {
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
		private Context context;
		private CharSequence [] items = {"Brand", "Brandbil", "Situation", "Resurs"};
		private String value;
		private EditText input;
		private Fire f;

		public Touchy(Context context){
			this.context = context;
		}

		public boolean onTouchEvent(MotionEvent e, MapView m) {

			if(e.getAction() == MotionEvent.ACTION_DOWN){
				start = e.getEventTime();
				touchedX = (int) e.getX();
				touchedY = (int) e.getY();
				touchedPoint = mapView.getProjection().fromPixels(touchedX, touchedY);
			}
			if(e.getAction() == MotionEvent.ACTION_UP){
				stop = e.getEventTime();
			}
			if(stop - start > 1000){


				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle("Placera");
				builder.setItems(items, new DialogInterface.OnClickListener() {


					public void onClick(DialogInterface dialog, int item) {

						AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

						alertDialog.setTitle("Lägg till beskrivning");
						alertDialog.setMessage("Beskrivning");

						input = new EditText(context);
						alertDialog.setView(input);

						
						alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								value = input.getText().toString();
								Toast.makeText(getApplicationContext(), value, Toast.LENGTH_LONG).show();
								f = new Fire(touchedPoint, value, "00000", SituationPriority.HIGH);
								mapCont.add(f);
							}
						});

						alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {

							}
						});


						alertDialog.show();

						if(item == 0){		
							//mapCont.add(new Fire(touchedPoint, value, "00000", SituationPriority.HIGH));
						}
						if(item == 1){
							mapCont.add(new FireTruck(touchedPoint, "Vi är på väg", "00000", ResourceStatus.BUSY));
						}
						if(item == 2){
							mapCont.add(new Situation(touchedPoint, "Situation", "Något har hänt", R.drawable.situation, "00000", SituationPriority.NORMAL));
						}
						if(item == 3){
							mapCont.add(new Resource(touchedPoint, "Resurs", "Räddningen är här", R.drawable.resource, "00000", ResourceStatus.BUSY));
						}
						Toast.makeText(getApplicationContext(), items[item]+" utplacerad", Toast.LENGTH_LONG).show();

					}

				});
				AlertDialog alert = builder.create();




				//				AlertDialog alert = new AlertDialog.Builder(MapUI.this).create();
				//				alert.setTitle("Karta");
				//				alert.setMessage("Tryck på en knapp");
				//				
				//				
				//				
				//				alert.setButton("Brand", new DialogInterface.OnClickListener() {
				//					public void onClick(DialogInterface dialog, int which) {
				//						
				//						
				//						
				//						//						Fire f = new Fire(touchedPoint, "Det brinner här!", "00000", SituationPriority.HIGH);
				//						//						d = getResources().getDrawable(f.getIcon());
				//						//						MapObjectList firePlaces = new MapObjectList(d, MapUI.this);
				//						//						firePlaces.addOverlay(f);
				//						//						mapOverlays.add(firePlaces);
				//
				//						mapCont.add(new Fire(touchedPoint, "Det brinner här!", "00000", SituationPriority.HIGH));
				//
				//						
				//					}
				//				});
				//
				//				alert.setButton2("Brandbil", new DialogInterface.OnClickListener() {
				//					public void onClick(DialogInterface dialog, int which) {
				//						FireTruck f = new FireTruck(touchedPoint, "Här kommer hjälpen!", "00000", ResourceStatus.FREE);
				//						d = getResources().getDrawable(f.getIcon());
				//						MapObjectList fireTrucks = new MapObjectList(d, MapUI.this);
				//						fireTrucks.addOverlay(f);
				//						mapOverlays.add(fireTrucks);
				//					}
				//				});
				//
				//
				//				// SYNS INTE I MENYN, FÅR ENDAST PLATS TRE ALTERNATIV
				//				alert.setButton3("get adress", new DialogInterface.OnClickListener() {
				//					public void onClick(DialogInterface dialog, int wich){
				//						Fire f = new Fire(touchedPoint, "Det brinner här!", "00000", SituationPriority.HIGH);
				//						//mapCont.add(f);
				//						MapObjectList firePlaces = new MapObjectList(d, MapUI.this);
				//						firePlaces.addOverlay(f);
				//						mapOverlays.add(firePlaces);
				//
				//
				//					}
				//				});
				//
				//
				//				alert.setButton2("get adress", new DialogInterface.OnClickListener() {
				//					public void onClick(DialogInterface dialog, int which) {
				//						Geocoder geocoder = new Geocoder(getBaseContext(), Locale.getDefault());
				//						try{
				//							List<Address> address = geocoder.getFromLocation(touchedPoint.getLatitudeE6() / 1E6, touchedPoint.getLongitudeE6() / 1E6, 1);
				//							if(address.size() > 0){
				//								String display ="";
				//								for(int i = 0; i<address.get(0).getMaxAddressLineIndex(); i++){
				//									display += address.get(0).getAddressLine(i) + "\n";
				//								}
				//								Toast t = Toast.makeText(getBaseContext(), display, Toast.LENGTH_LONG);
				//								t.show();
				//							}
				//						} catch (IOException e) {
				//							// TODO Auto-generated catch block
				//							e.printStackTrace();
				//						}finally{
				//
				//						}
				//					}
				//				});
				//
				//
				alert.setButton("Toggle View", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if(mapView.isSatellite()){
							mapView.setSatellite(false);
						}
						else{
							mapView.setSatellite(true);
						}
					}
				});


				alert.setButton2("Gå till min position", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						gps = new GPSModel(MapUI.this);
						you.setPoint(myLocation);
						controller.animateTo(myLocation);										
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
		if (data instanceof GeoPoint){
			you.setPoint((GeoPoint)data);
			myLocation = (GeoPoint) data;
		}
		if (data instanceof MapObjectList){
			mapOverlays.add((MapObjectList) data);	
		}
		mapView.postInvalidate();
		// RITA OM PÅ NÅGOT SÄTT
		//använd mapView.invalidate() om du kör i UI tråden
	}

}
