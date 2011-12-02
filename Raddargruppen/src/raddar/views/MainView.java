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
import raddar.models.Message;
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
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
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
	private ImageButton connectionButton;
	private Bundle extras;
	public static MapCont mapCont;
	public static MainView theOne;

	/*
	 * Lyssnar efter ändringar hos batterinivån
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
		setContentView(R.layout.main);
		SessionController.titleBar(this, " ");

		theOne = this;
		
		new DatabaseController(this);
		DatabaseController.db.addObserver(this);
		/**
		 * Initierar kartans controller för att kunna få gps koordinaterna för sin position
		 */
		new Thread(new Runnable() {
			public void run(){
				mapCont = new MapCont(MainView.this);
			}
		}).start();
		
		extras = getIntent().getExtras();
		new SessionController(extras.get("user").toString());
		new SipController(this);
		new ReciveHandler(this).addObserver(this);
		
		String level = BatteryManager.EXTRA_LEVEL;
		Log.d("EXTRA_LEVEL", level);

		//		controller = new InternalComManager();
		//		controller.setUser(extras.get("user").toString());
		//		db = new ClientDatabaseManager(this,controller.getUser());

		//		//TEMPORÄRT MÅSTE FIXAS
		//		NotificationMessage nm = new NotificationMessage(MainView.controller.getUser(), NotificationType.CONNECT);

		try {
			new Sender(new RequestMessage(RequestType.MESSAGE));
			new Sender(new RequestMessage(RequestType.BUFFERED_MESSAGE));
			DatabaseController.db.clearTable("contact");
			new Sender(new RequestMessage(RequestType.CONTACTS));
			new Sender(new RequestMessage(RequestType.MAP_OBJECTS));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

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

		connectionButton = (ImageButton) findViewById(R.id.presence);
		connectionButton.setOnClickListener(this);
		if (extras.get("connectionStatus").equals(ConnectionStatus.CONNECTED)){
			connectionButton.setImageResource(R.drawable.connected);
		}
		else if (extras.get("connectionStatus").equals(ConnectionStatus.DISCONNECTED)){
			connectionButton.setImageResource(R.drawable.disconnected);
		}
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
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Är du säker på att du vill logga ut?")
			.setCancelable(false)
			.setPositiveButton("Ja", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int id) {
					// Notifiera servern att vi går offline
					NotificationMessage nm = new NotificationMessage(SessionController.getUser(), 
							NotificationType.DISCONNECT);
					try {
						// Skicka meddelandet
						new Sender(nm);		
					} catch (UnknownHostException e) {
						Log.d("NotificationMessage", "Disconnect failed");
					}
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
		else if (v == connectionButton){
			Toast.makeText(getBaseContext(), extras.get("connectionStatus").toString()+", inloggad som: "
					+SessionController.getUser(), Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		SipController.onClose();
		// Notifiera servern att vi går offline
		NotificationMessage nm = new NotificationMessage(SessionController.getUser(), 
				NotificationType.DISCONNECT);
		try {
			// Skicka meddelandet
			new Sender(nm);		
			DatabaseController.db.clearDatabase();
		} catch (UnknownHostException e) {
			Log.d("NotificationMessage", "Disconnect failed");
		}

		/* Om applikationen stängs ner tar vi bort notifikationer i 
		   notifikationsfältet längst upp på telefonens skärm */
		NotificationManager mNtf = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		mNtf.cancelAll();
		DatabaseController.db.close();
		if (SettingsView.powerIsAutomatic())
			unregisterReceiver(mBatteryInfoReceiver);
	}

	public void update(Observable observable, final Object data) {
		runOnUiThread(new Runnable(){
			public void run(){
				if(data != null && data instanceof Message)
					Toast.makeText(getApplicationContext(), "Meddelande från "+
							((Message)data).getSrcUser()
							,Toast.LENGTH_LONG).show();

				if(data == ConnectionStatus.CONNECTED){
					connectionButton.setImageResource(R.drawable.connected);
					Toast.makeText(getApplicationContext(), "Ansluten till servern, inloggad som: "+SessionController.getUser()
							, Toast.LENGTH_LONG).show();
				}else if (data == ConnectionStatus.DISCONNECTED){
					connectionButton.setImageResource(R.drawable.disconnected);
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
		//unregisterReceiver(mBatteryInfoReceiver);
	}

	public void enableButtons() {
		callButton.setEnabled(true);
		serviceButton.setEnabled(true);
	}

	public void disableButtons() {
		callButton.setEnabled(false);
		serviceButton.setEnabled(false);
	}
}