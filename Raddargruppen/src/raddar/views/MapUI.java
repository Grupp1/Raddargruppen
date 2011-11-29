package raddar.views;

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
import raddar.models.MapObject;
import raddar.models.MapObjectList;
import raddar.models.QoSManager;
import raddar.models.Resource;
import raddar.models.Situation;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
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
	public MapController controller;
	private int touchedX, touchedY;
	private GeoPoint touchedPoint, liu, myLocation, sthlmLocation;
	private List<Overlay> mapOverlays;
	private Touchy touchy;
	public boolean follow;
	private Toast toast;
	private Geocoder geocoder;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.maps);
		
		
		
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

		/**
		 * Random locations
		 */
		myLocation = new GeoPoint(0,0);
		liu = new GeoPoint(58395730, 15573080);
		sthlmLocation = new GeoPoint(59357290, 17960050);

		geocoder = new Geocoder(getBaseContext(), Locale.getDefault());
		
		touchy = new Touchy(mapView.getContext());
		mapOverlays.add(touchy);
		
		MainView.mapCont.declareMapUI(this);

		controller.animateTo(sthlmLocation);
		controller.setZoom(8);

	}

	@Override
	protected void onStart() {
		if(MainView.mapCont.areYouFind){
			follow = true;
			controller.animateTo(MainView.mapCont.getYou().getPoint());
			controller.setZoom(12);
		}
		super.onStart();
	}

	@Override
	protected void onResume() {
		compass.enableCompass();
		super.onResume();
		QoSManager.setCurrentActivity(this);
		QoSManager.setPowerMode();
		MainView.mapCont.gps.getLocationManager().requestLocationUpdates(MainView.mapCont.gps.getTowers(), 500, 1, MainView.mapCont.gps);
	}

	@Override
	protected void onPause() {
		compass.disableCompass();
		super.onPause();
		MainView.mapCont.gps.getLocationManager().removeUpdates(MainView.mapCont.gps);
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy(){
		super.onStop();
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	// Tar hand om inmatning från skärmen, ritar ut knappar och anropar MapCont
	
	class Touchy extends Overlay{
		private Context context;
		private CharSequence [] items = {"Brand", "Brandbil", "Situation", "Resurs"};
		private String value;
		private EditText input;
		private int item;

		public Touchy(Context context){
			this.context = context;
		}

		public boolean onTouchEvent(MotionEvent e, MapView m) {
			int holdTime = 800;
			if(e.getAction() == MotionEvent.ACTION_DOWN){
				start = e.getEventTime();
				touchedX = (int) e.getX();
				touchedY = (int) e.getY();
				touchedPoint = mapView.getProjection().fromPixels(touchedX, touchedY);				
			}
			if(e.getAction() == MotionEvent.ACTION_UP){
				stop = e.getEventTime();
			}
			if(stop - start > holdTime){

				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle("Placera");
				builder.setItems(items, new DialogInterface.OnClickListener() {


					public void onClick(DialogInterface dialog, int item) {
						Touchy.this.item = item;
						AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

						alertDialog.setTitle("Lägg till beskrivning");
						alertDialog.setMessage("Beskrivning");

						input = new EditText(context);
						alertDialog.setView(input);

						alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								value = input.getText().toString();
								MapObject o = null;
								if(Touchy.this.item == 0){		
									MainView.mapCont.add(o = new Fire(touchedPoint, value, SituationPriority.HIGH),true);
								}
								if(Touchy.this.item == 1){
									MainView.mapCont.add(o = new FireTruck(touchedPoint, value, ResourceStatus.BUSY),true);
								}
								if(Touchy.this.item == 2){
									MainView.mapCont.add(o = new Situation(touchedPoint, "Situation", value, R.drawable.situation, SituationPriority.NORMAL),true);
								}
								if(Touchy.this.item == 3){
									MainView.mapCont.add(o = new Resource(touchedPoint, "Resurs", value, R.drawable.resource, ResourceStatus.BUSY),true);
								}
								o.updateData(geocoder);;
								Toast.makeText(getApplicationContext(), items[Touchy.this.item]+" utplacerad", Toast.LENGTH_LONG).show();
							}
						});
						alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {

							}
						});
						alertDialog.show();
					}

				});

				AlertDialog alert = builder.create();

				alert.setButton("Hämta adress", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Toast.makeText(getApplicationContext(), MainView.mapCont.calcAdress(touchedPoint), Toast.LENGTH_LONG).show();
					}
				});

				/*
				// Exempel på en till knapp
				alert.setButton2("Gå till min position", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						controller.animateTo(myLocation); 
					}
				});
				 */

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

	public MapView getMapView(){
		return mapView;
	}
	
	public void updateMyLocation(GeoPoint geopoint){
		
	}
	
	public void drawNewMapObject(MapObject mo){
		MapObjectList list = MainView.mapCont.getList(mo);
		if(list == null){
			Log.d("MapUI", "list är null");
			return;
		}
		if (!mapOverlays.contains(list)){
			mapOverlays.add((MapObjectList) list);
		}
		else{
			mapOverlays.set(mapOverlays.indexOf(list), list);
		}
		mapView.postInvalidate();
	}
	
	public void update(Observable observable, Object data) {
		Log.d("MAPUI",observable.toString());
		if (data instanceof GeoPoint){

		}
		else if (data instanceof MapObjectList){
			if (!mapOverlays.contains(data)){
				mapOverlays.add((MapObjectList) data);
			}
			else{
				mapOverlays.set(mapOverlays.indexOf(data), (MapObjectList)data);
			}
		//	mapOverlays.add((MapObjectList) data);
		}
		else if(data instanceof MapObject){
			MapObjectList list = MainView.mapCont.getList((MapObject)data);
			if(list == null){
				Log.d("MapUI", "list är null");
				return;
			}
			if (!mapOverlays.contains(list)){
				mapOverlays.add((MapObjectList) list);
			}
			else{
				mapOverlays.set(mapOverlays.indexOf(list), list);
			}
		}
			
		mapView.postInvalidate();
		// RITA OM PÅ NÅGOT SÄTT
		//använd mapView.invalidate() om du kör i UI tråden
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mapmenu, menu);
		return true;
	}

	//	Anropas varje gång menu klickas på
	//	@Override
	//	public boolean onPrepareOptionsMenu(Menu menu){
	//		return true;
	//	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.toggle:
			if(mapView.isSatellite()){
				mapView.setSatellite(false);
			}
			else{
				mapView.setSatellite(true);
			}
			return true;
		case R.id.follow:
			if (follow){
				follow = false;
			}
			else{
				follow = true;
			}
			toast = Toast.makeText(getBaseContext(), "Följ efter: " +follow, Toast.LENGTH_LONG);
			toast.show();
			return true;
		case R.id.myLocation:
			if (MainView.mapCont.areYouFind){
				controller.animateTo(MainView.mapCont.getYou().getPoint());
			}else{
				toast = Toast.makeText(getBaseContext(), "Kan ej hitta position", Toast.LENGTH_LONG);
				toast.show();
			}
			return true;
		case R.id.traffic:
			if(mapView.isTraffic()){
				mapView.setTraffic(false);
			}
			else{
				mapView.setTraffic(true);
			}
			toast = Toast.makeText(getBaseContext(), "Trafik: " +mapView.isTraffic(), Toast.LENGTH_LONG);
			toast.show();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
