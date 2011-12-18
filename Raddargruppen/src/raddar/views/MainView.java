package raddar.views;

import java.net.UnknownHostException;
import java.util.Observable;
import java.util.Observer;

import raddar.controllers.DatabaseController;
import raddar.controllers.MapCont;
import raddar.controllers.ReciveHandler;
import raddar.controllers.Sender;
import raddar.controllers.SessionController;
import raddar.controllers.SipController;
import raddar.enums.ConnectionStatus;
import raddar.enums.NotificationType;
import raddar.enums.RequestType;
import raddar.gruppen.R;
import raddar.models.NotificationMessage;
import raddar.models.QoSManager;
import raddar.models.RequestMessage;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainView extends Activity implements OnClickListener, Observer {

	private ImageButton callButton;
	private ImageButton messageButton;
	private ImageButton mapButton;
	private ImageButton contactButton;
	private ImageButton serviceButton;
	private ImageButton sosButton;
	private ImageButton setupButton;
	private ImageButton logButton;
	public static MapCont mapCont;
	public static MainView theOne;
	private Bundle extras;
	private ProgressBar downloadBar;
	private int max;
	private ReciveHandler reciveHandler;

	/*
	 * Lyssnar efter �ndringar hos batteriniv�n
	 */
	public BroadcastReceiver mBatteryInfoReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
				int level = intent.getIntExtra("level", 0);
				Log.d("BATTERY LEVEL:", ""+level);
				int scale = intent.getIntExtra("scale", 100);
				int true_level = level * 100 / scale;
				QoSManager.setPowerMode(true_level);
			}
		}
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_RIGHT_ICON);
		setContentView(R.layout.main);

		
		extras = getIntent().getExtras();
		theOne = this;

		DatabaseController.db.addObserver(this);
		/**
		 * Initierar kartans controller f�r att kunna f� gps koordinaterna f�r sin position
		 */
		new SessionController(extras.get("user").toString()).addObserver(this);
		SessionController.appIsRunning = true;
		SessionController.titleBar(this, " - Huvudmeny", true);
		
		new SipController(this);
		reciveHandler = new ReciveHandler(this);
		reciveHandler.addObserver(this);

		String level = BatteryManager.EXTRA_LEVEL;
		Log.d("EXTRA_LEVEL", level);


		//		controller = new InternalComManager();
		//		controller.setUser(extras.get("user").toString());
		//		db = new ClientDatabaseManager(this,controller.getUser());

		//		//TEMPOR�RT M�STE FIXAS
		//		NotificationMessage nm = new NotificationMessage(MainView.controller.getUser(), NotificationType.CONNECT);

		//		new DatabaseController(this);



		//		try {
		//			new Sender(new RequestMessage(RequestType.MESSAGE));
		//			new Sender(new RequestMessage(RequestType.BUFFERED_MESSAGE));
		//			new Sender(new RequestMessage(RequestType.CONTACTS));
		//			new Sender(new RequestMessage(RequestType.MAP_OBJECTS));
		//		} catch (UnknownHostException e) {
		//			e.printStackTrace();
		//		}



		callButton = (ImageButton)this.findViewById(R.id.callButton);
		callButton.setOnClickListener(this);

		messageButton = (ImageButton)this.findViewById(R.id.messageButton);
		messageButton.setOnClickListener(this);

		mapButton = (ImageButton)this.findViewById(R.id.mapButton);
		mapButton.setOnClickListener(this);

		contactButton = (ImageButton)this.findViewById(R.id.contactButton);
		contactButton.setOnClickListener(this);

		serviceButton = (ImageButton)this.findViewById(R.id.serviceButton);
		serviceButton.setOnClickListener(this);

		sosButton = (ImageButton)this.findViewById(R.id.sosButton);
		sosButton.setOnClickListener(this);

		setupButton = (ImageButton)this.findViewById(R.id.setupButton);
		setupButton.setOnClickListener(this);

		logButton = (ImageButton)this.findViewById(R.id.logButton);
		logButton.setOnClickListener(this);

		downloadBar = (ProgressBar) this.findViewById(R.id.download_bar);
		downloadBar.setVisibility(View.INVISIBLE);



		QoSManager.setCurrentActivity(this);
		QoSManager.setPowerMode();
		mapCont = new MapCont(MainView.this);
		SessionController.getSessionController().changeConnectionStatus((ConnectionStatus)extras.get("connectionStatus"));
	}

	public void onClick(View v) {
		if(v == callButton){
			if(!QoSManager.power_save){
				Intent nextIntent = new Intent(MainView.this, CallContactListView.class);
				startActivity(nextIntent);
			}
			else{
				Toast.makeText(getBaseContext(), "Funktionen inte tillgänglig under strömsparsläge"
						, Toast.LENGTH_SHORT).show();
			}
		}
		else if(v == messageButton){
			Intent nextIntent = new Intent(MainView.this, MessageChoiceView.class);
			startActivity(nextIntent);
		}
		else if(v == mapButton){
			Intent nextIntent = new Intent(MainView.this, MapUI.class);
			startActivity(nextIntent);
		}
		else if(v == contactButton){
			if(!QoSManager.power_save){
				Intent nextIntent = new Intent(MainView.this, ContactListView.class);
				startActivity(nextIntent);
			}
			else{
				Toast.makeText(getBaseContext(), "Funktionen inte tillgänglig under strömsparsläge"
						, Toast.LENGTH_SHORT).show();
			}
		}
		else if(v == serviceButton){
			if(!QoSManager.power_save){
				Intent nextIntent = new Intent(MainView.this, ServiceView.class);
				startActivity(nextIntent);
			}
			else{
				Toast.makeText(getBaseContext(), "Funktionen inte tillgänglig under strömsparsläge"
						, Toast.LENGTH_SHORT).show();
			}
		}
		else if(v == sosButton){
			Intent nextIntent = new Intent(MainView.this, SendSOSView.class);
			startActivity(nextIntent);
		}
		else if(v == setupButton){
			Intent nextIntent = new Intent(MainView.this, SettingsView.class);
			startActivity(nextIntent);
		}
		else if(v == logButton){
			showLogoutWindow();
		}

	}
	private void showLogoutWindow(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Är du säker på att du vill logga ut?")
		.setCancelable(false)
		.setPositiveButton("Ja", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int id) {
				MainView.this.finish();
			}
		})
		.setNegativeButton("Nej", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	@Override
	public void onBackPressed() {
		showLogoutWindow();
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		SessionController.getSessionController().deleteObserver(this);
		SessionController.appIsRunning = false;
		
		SipController.onClose();
		MainView.mapCont.gps.getLocationManager().removeUpdates(MainView.mapCont.gps);
		// Notifiera servern att vi g�r offline
		NotificationMessage nm = new NotificationMessage(SessionController.getUser(), 
				NotificationType.DISCONNECT);
		try {
			// Skicka meddelandet
			new Sender(nm);		

		} catch (UnknownHostException e) {
			Log.d("NotificationMessage", "Disconnect failed");
		}
		/* Om applikationen st�ngs ner tar vi bort notifikationer i 
		   notifikationsf�ltet l�ngst upp p� telefonens sk�rm */
		NotificationManager mNtf = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		mNtf.cancelAll();
		if (SettingsView.powerIsAutomatic())
			unregisterReceiver(mBatteryInfoReceiver);
	}


	public void update(Observable observable, final Object data) {
		runOnUiThread(new Runnable(){
			public void run(){
				if(data == ConnectionStatus.CONNECTED){
					Log.d("STATUS","CONNECTED");
					Toast.makeText(getBaseContext(), "Ansluten till servern"
							, Toast.LENGTH_LONG).show();
					mapCont.renewYou();
					downloadBar.setProgress(0);
					downloadBar.setVisibility(View.VISIBLE);
					//timer.schedule(new CountDown(), 30*1000,30*1000);
					try {
						//new Sender(new RequestMessage(RequestType.MESSAGE));
						//new Sender(new RequestMessage(RequestType.BUFFERED_MESSAGE));
						//DatabaseController.db.clearTable("contact");
						//new Sender(new RequestMessage(RequestType.CONTACTS));
						//new Sender(new RequestMessage(RequestType.MAP_OBJECTS));
						//new Sender(new RequestMessage(RequestType.ONLINE_CONTACTS));
						new Sender(new RequestMessage(RequestType.NEW_LOGIN));
					} catch (UnknownHostException e) {
						e.printStackTrace();
					}

				}else if (data == ConnectionStatus.DISCONNECTED){
					Log.d("STATUS","DISCONNECTED");
					SessionController.getSessionController().clearOnlineUsers();
					Toast.makeText(getBaseContext(), "Tappad anslutning mot servern",Toast.LENGTH_LONG).show();
				}
				else if (data instanceof String){
					if(data.equals("LOGOUT")){
						SessionController.getSessionController().changeConnectionStatus(ConnectionStatus.DISCONNECTED);
						finish();
					}else{
						Toast.makeText(getBaseContext(), (String)data, Toast.LENGTH_SHORT).show();
					}
				}
				else if (data instanceof Integer){
					final int progress = ((Integer)data).intValue();
					if (progress < 0){
						DatabaseController.db.clearDatabase();
						downloadBar.setVisibility(View.VISIBLE);
						max = -progress;
						downloadBar.setMax(max);
						downloadBar.setProgress(0);

					}
					else if(progress < max){
						downloadBar.setProgress(progress);
						downloadBar.postInvalidate();
					}
					else{
						downloadBar.setVisibility(View.INVISIBLE);
					}
				}

			}
		});

	}
	
	@Override
	public void onResume() {
		super.onResume();
		QoSManager.setCurrentActivity(this);
		QoSManager.setPowerMode();
		MainView.mapCont.gps.getLocationManager().requestLocationUpdates(MainView.mapCont.gps.getTowers(), 500, 1, MainView.mapCont.gps);
		//Session
		if (SettingsView.powerIsAutomatic())
			registerReceiver(mBatteryInfoReceiver, new IntentFilter(
					Intent.ACTION_BATTERY_CHANGED));
		SessionController.getSessionController().updateConnectionImage();
	}

	@Override
	public void onPause() {
		super.onPause();
	}
	
	public void viewToast(final String text){
		runOnUiThread(new Runnable(){
			public void run(){
				Toast toast = Toast.makeText(getBaseContext(), text, Toast.LENGTH_LONG);
				toast.show();
			}
		});
	}
}