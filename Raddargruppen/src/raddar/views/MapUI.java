package raddar.views;

import java.util.List;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

import raddar.controllers.MapCont;
import raddar.controllers.SessionController;
import raddar.enums.ResourceStatus;
import raddar.enums.SituationPriority;
import raddar.gruppen.R;
import raddar.models.MapObject;
import raddar.models.MapObjectList;
import raddar.models.QoSManager;
import raddar.models.Resource;
import raddar.models.Situation;
import raddar.models.You;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Window;
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
	private Toast toast;
	private Geocoder geocoder;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_RIGHT_ICON);
		setContentView(R.layout.maps);

		SessionController.titleBar(this, " - Karta", true);
		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		mapView.setSatellite(true);
		/**
		 *  Lista �ver alla overlays (lager) som visas p� kartan
		 */
		mapOverlays = mapView.getOverlays();
		touchy = new Touchy(mapView.getContext());
		mapOverlays.add(touchy);

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

		MainView.mapCont.declareMapUI(this);

		controller.animateTo(sthlmLocation);
		controller.setZoom(8);

	}


	@Override
	protected void onStart() {
		super.onStart();
		Bundle extras = getIntent().getExtras();
		if(extras != null && extras.containsKey("lat")&& extras.containsKey("lon")){
			MainView.mapCont.follow = false;
			controller.animateTo(new GeoPoint(extras.getInt("lat"),extras.getInt("lon")));
			controller.setZoom(18);
		}
		else if(MainView.mapCont.areYouFind){
			MainView.mapCont.follow = true;
			controller.animateTo(MainView.mapCont.getYou().getPoint());
			controller.setZoom(15);
		}
	}

	@Override
	protected void onResume() {
		compass.enableCompass();
		super.onResume();
		QoSManager.setCurrentActivity(this);
		QoSManager.setPowerMode();
		SessionController.getSessionController().updateConnectionImage();
	}

	@Override
	protected void onPause() {
		super.onPause();
		compass.disableCompass();
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

	public void sendTextMessage(String user){
		Intent nextIntent = new Intent(MapUI.this,
				SendMessageView.class);
		String [] items = {user, "", ""};
		nextIntent.putExtra("message", items);
		startActivity(nextIntent);
	}

	public void sendImageMessage(String user){
		Intent nextIntent = new Intent(MapUI.this,
				SendImageMessageView.class);
		String [] items = {user, "", ""};
		nextIntent.putExtra("message", items);
		startActivity(nextIntent);
	}

	public void callUser(String user){
		Intent nextIntent = new Intent(MapUI.this,
				CallView.class);
		nextIntent.putExtra("sip", "sip:" + user + "@ekiga.net");
		nextIntent.putExtra("dstUser", user);
		startActivity(nextIntent);
	}

	// Tar hand om inmatning fr�n sk�rmen, ritar ut knappar och anropar MapCont

	class Touchy extends Overlay{
		private Context context;
		//		private CharSequence [] items = {"Brand", "Brandbil", "H�ndelse", "Resurs"};
		private CharSequence [] items = {"Situation", "Resurs"};
		private CharSequence [] prio = {"Hög", "Mellan", "Låg"};
		private CharSequence [] stat = {"Ledig", "Upptagen"};
		private String value;
		private EditText input;
		private int item;
		private int prioritet;
		private int status;
		private MapObject o = null;
		private Handler handler = new Handler();
		private Runnable showMenu = new Runnable() {
			public void run() {
				Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
				vib.vibrate(100);
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

								/*
								 * Om situation s�tt prioritet
								 */

								if(Touchy.this.item == 0){
									AlertDialog.Builder builder = new AlertDialog.Builder(context);
									builder.setTitle("Välj prioritet");
									builder.setSingleChoiceItems(prio, -1, new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int item) {
											prioritet = item;
										}
									});

									builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int whichButton) {
											SituationPriority prio = SituationPriority.NORMAL;
											if(Touchy.this.prioritet == 0){
												prio = SituationPriority.HIGH;
											}
											if(Touchy.this.prioritet == 1){
												prio = SituationPriority.NORMAL;
											}
											if(Touchy.this.prioritet == 2){
												prio = SituationPriority.LOW;
											}
											o = new Situation(touchedPoint, "Situation", value,
													R.drawable.situation, prio);
											o.updateData(geocoder);
											MainView.mapCont.add(o, true);
										}
									});

									builder.setNegativeButton("Avbryt", new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int whichButton) {

										}
									});
									builder.show();
								}



								/*
								 * Om resurs, s�tt prioritet
								 */

								if(Touchy.this.item == 1){
									AlertDialog.Builder builder = new AlertDialog.Builder(context);
									builder.setTitle("Välj status");
									builder.setSingleChoiceItems(stat, -1, new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int item) {
											status = item;
										}
									});

									builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int whichButton) {
											status = whichButton;
											ResourceStatus status = ResourceStatus.FREE;
											if(Touchy.this.status == 0){
												status = ResourceStatus.FREE;
											}
											if(Touchy.this.status == 1){
												status = ResourceStatus.BUSY;
											}
											o = new Resource(touchedPoint, "Resurs", value,
													R.drawable.resource, status);
											o.updateData(geocoder);
											MainView.mapCont.add(o, true);
										}
									});

									builder.setNegativeButton("Avbryt", new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int whichButton) {

										}
									});

									builder.show();

								}
							}
						});
						alertDialog.setNegativeButton("Avbryt", new DialogInterface.OnClickListener() {
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
				// Exempel p� en till knapp
				alert.setButton2("G� till min position", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						controller.animateTo(myLocation); 
					}
				});
				 */
				alert.show();
			}
		};
		public Touchy(Context context){
			this.context = context;
		}

		public boolean onTouchEvent(MotionEvent e, MapView m) {
			int holdTime = 750;
			if(e.getAction() == MotionEvent.ACTION_DOWN){
				start = e.getEventTime();
				touchedX = (int) e.getX();
				touchedY = (int) e.getY();
				touchedPoint = mapView.getProjection().fromPixels(touchedX, touchedY);
				handler.postDelayed(showMenu, holdTime);
			}
			if(e.getAction() == MotionEvent.ACTION_UP){
				handler.removeCallbacks(showMenu);
			}
			if (e.getAction() == MotionEvent.ACTION_MOVE) {
				int x2 = (int) e.getX();
				int y2 = (int) e.getY();
				if ((Math.abs(touchedX - x2) > 15) || (Math.abs(touchedY - y2) > 15)) {
					handler.removeCallbacks(showMenu);
				}
			}

			return false;

		}
	}

	public MapView getMapView(){
		return mapView;
	}

	public void updateMyLocation(GeoPoint geopoint){

	}

	/**
	 * 
	 * @param mo MapObject att rita ut
	 * @param hideToast true om en toast inte ska visas f�r uppdateringen
	 */
	public void drawNewMapObject(final MapObject mo){
		runOnUiThread(new Runnable(){
			public void run() {
				MapObjectList list = MainView.mapCont.getList(mo);
				if (!mapOverlays.contains(list)){
					mapOverlays.add((MapObjectList) list);
				}
				else{
					mapOverlays.set(mapOverlays.indexOf(list), list);
				}
				mapView.invalidate();
			}});
	}
	
	public void update(Observable observable, Object data) {
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
		// RITA OM P� N�GOT S�TT
		//anv�nd mapView.invalidate() om du k�r i UI tr�den
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mapmenu, menu);
		return true;
	}

	//	Anropas varje g�ng menu klickas p�
	//	@Override
	//	public boolean onPrepareOptionsMenu(Menu menu){
	//		return true;
	//	}

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
			if (MainView.mapCont.follow){
				MainView.mapCont.follow = false;
				toast = Toast.makeText(getBaseContext(), "Ej fokuserad på din position", Toast.LENGTH_LONG);
			}
			else{
				MainView.mapCont.follow = true;
				toast = Toast.makeText(getBaseContext(), "Fokuserad på din position", Toast.LENGTH_LONG);
			}
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
				toast = Toast.makeText(getBaseContext(), "Trafik döljs", Toast.LENGTH_LONG);
			}
			else{
				mapView.setTraffic(true);
				toast = Toast.makeText(getBaseContext(), "Trafik visas", Toast.LENGTH_LONG);
			}
			toast.show();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
