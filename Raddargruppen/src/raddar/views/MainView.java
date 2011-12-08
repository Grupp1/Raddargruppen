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
	private TextView statusText;
	public static MapCont mapCont;
	public static MainView theOne;
	private Bundle extras;

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
		
		SessionController.titleBar(this, " ");
		extras = getIntent().getExtras();
		theOne = this;

		DatabaseController.db.addObserver(this);
		/**
		 * Initierar kartans controller f�r att kunna f� gps koordinaterna f�r sin position
		 */
		new SessionController(extras.get("user").toString()).addObserver(this);
		mapCont = new MapCont(MainView.this);
		new SipController(this);
		new ReciveHandler(this).addObserver(this);

		String level = BatteryManager.EXTRA_LEVEL;
		Log.d("EXTRA_LEVEL", level);
		

		//		controller = new InternalComManager();
		//		controller.setUser(extras.get("user").toString());
		//		db = new ClientDatabaseManager(this,controller.getUser());

		//		//TEMPOR�RT M�STE FIXAS
		//		NotificationMessage nm = new NotificationMessage(MainView.controller.getUser(), NotificationType.CONNECT);

		new SessionController(extras.get("user").toString()).addObserver(this);
		//		new DatabaseController(this);
		new ReciveHandler(this).addObserver(this);
		
		
		
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
		

		statusText = (TextView)this.findViewById(R.id.statusText);
		
		statusText.setText("Inloggad som: " +  SessionController.getUser());
		statusText.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC), Typeface.ITALIC);
		
		QoSManager.setCurrentActivity(this);
		QoSManager.setPowerMode();
		SessionController.getSessionController().changeConnectionStatus((ConnectionStatus)extras.get("connectionStatus"));
	}

	public void onClick(View v) {
		if(v == callButton){

			Intent nextIntent = new Intent(MainView.this, CallContactListView.class);
			startActivity(nextIntent);
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
			Intent nextIntent = new Intent(MainView.this, ContactListView.class);
			startActivity(nextIntent);
		}
		else if(v == serviceButton){
			Intent nextIntent = new Intent(MainView.this, ServiceView.class);
			startActivity(nextIntent);
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
		builder.setMessage("�r du s�ker p� att du vill logga ut?")
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
		SipController.onClose();
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
					Toast.makeText(getApplicationContext(), "Ansluten till servern"
							, Toast.LENGTH_LONG).show();
					DatabaseController.db.clearDatabase();
					mapCont.renewYou();
					try {
//						new Sender(new RequestMessage(RequestType.MESSAGE));
//						new Sender(new RequestMessage(RequestType.BUFFERED_MESSAGE));
//						DatabaseController.db.clearTable("contact");
//						new Sender(new RequestMessage(RequestType.CONTACTS));
//						new Sender(new RequestMessage(RequestType.MAP_OBJECTS));
//						new Sender(new RequestMessage(RequestType.ONLINE_CONTACTS));
						new Sender(new RequestMessage(RequestType.NEW_LOGIN));
					} catch (UnknownHostException e) {
						e.printStackTrace();
					}
					
				}else if (data == ConnectionStatus.DISCONNECTED){
					Log.d("STATUS","DISCONNECTED");
					Toast.makeText(getApplicationContext(), "Tappad anslutning mot servern",Toast.LENGTH_LONG).show();
				}
				else if (data instanceof String){
					if(data.equals("LOGOUT")){
						finish();
					}else{
						Toast.makeText(getBaseContext(), (String)data, Toast.LENGTH_SHORT).show();
					}
				}

			}
		});

	}

	@Override
	public void onResume() {
		super.onResume();
		if (SettingsView.powerIsAutomatic())
			registerReceiver(mBatteryInfoReceiver, new IntentFilter(
					Intent.ACTION_BATTERY_CHANGED));
		QoSManager.setCurrentActivity(this);
		QoSManager.setPowerMode();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	public void enableButtons() {
		callButton.setEnabled(true);
		serviceButton.setEnabled(true);
		contactButton.setEnabled(true);
	}

	public void disableButtons() {
		callButton.setEnabled(false);
		serviceButton.setEnabled(false);
		contactButton.setEnabled(false);
	}
}