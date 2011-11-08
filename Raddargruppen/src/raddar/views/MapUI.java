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
import raddar.models.You;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
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
	private MapController controller;
	private int touchedX, touchedY;
	private GeoPoint touchedPoint, liu, myLocation, sthlmLocation;
	private List<Overlay> mapOverlays;
	private GPSModel gps;
	private MapCont mapCont;
	private boolean follow;
	private You you;
	private Toast toast;

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

		myLocation = new GeoPoint(0,0);
		liu = new GeoPoint(58395730, 15573080);
		sthlmLocation = new GeoPoint(59357290, 17960050);

		you = new You(myLocation, "Min position", "Här är jag", R.drawable.niklas, "000000", ResourceStatus.FREE);
		gps = new GPSModel(this);

		mapCont = new MapCont(MapUI.this, you);
		
		controller.animateTo(myLocation);
		controller.setZoom(13);

		Touchy t = new Touchy(this);
		mapOverlays.add(t);

	}

	@Override
	protected void onStart() {
		follow = false;
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
		private int item;

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
			if(stop - start > 800){

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
								if(Touchy.this.item == 0){		
									mapCont.add(new Fire(touchedPoint, value, "00000", SituationPriority.HIGH));
								}
								if(Touchy.this.item == 1){
									mapCont.add(new FireTruck(touchedPoint, value, "00000", ResourceStatus.BUSY));
								}
								if(Touchy.this.item == 2){
									mapCont.add(new Situation(touchedPoint, "Situation", value, R.drawable.situation, "00000", SituationPriority.NORMAL));
								}
								if(Touchy.this.item == 3){
									mapCont.add(new Resource(touchedPoint, "Resurs", value, R.drawable.resource, "00000", ResourceStatus.BUSY));
								}
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
						Toast.makeText(getApplicationContext(), mapCont.calcAdress(touchedPoint), Toast.LENGTH_LONG).show();
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

	public void update(Observable observable, Object data) {
		if (data instanceof GeoPoint){
			you.setPoint((GeoPoint)data);
			myLocation = (GeoPoint) data;
			you.setPoint(myLocation);
			if (follow){
				controller.animateTo(myLocation);
			}
		}
		if (data instanceof MapObjectList){
			mapOverlays.add((MapObjectList) data);	
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
	    	controller.animateTo(myLocation); 
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
