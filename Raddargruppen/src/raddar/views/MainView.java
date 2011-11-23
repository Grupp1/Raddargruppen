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
import raddar.models.RequestMessage;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;

public class MainView extends Activity implements OnClickListener, Observer{

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
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		
		extras = getIntent().getExtras();
//		controller = new InternalComManager();
//		controller.setUser(extras.get("user").toString());
//		db = new ClientDatabaseManager(this,controller.getUser());
		
//		//TEMPOR�RT M�STE FIXAS
//		NotificationMessage nm = new NotificationMessage(MainView.controller.getUser(), NotificationType.CONNECT);

		
		
		new SessionController(extras.get("user").toString());
		new DatabaseController(this);
		new SipController(this);
		new ReciveHandler(this).addObserver(this);


		

		try {
			new Sender(new RequestMessage(RequestType.MESSAGE));
			new Sender(new RequestMessage(RequestType.BUFFERED_MESSAGE));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		DatabaseController.db.addObserver(this);

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
		
		/**
		 * Initierar kartans controller f�r att kunna f� gps koordinaterna f�r sin position
		 */
		new Thread(new Runnable() {
			public void run(){
				mapCont = new MapCont(MainView.this);
			}
		}).start();
		
	}

	public void onClick(View v) {

		if(v == callButton){
			Intent nextIntent = new Intent(this,EnterNumberView.class);
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

		}
		else if(v == setupButton){

		}
		else if(v == logButton){
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("�r du s�ker p� att du vill logga ut?")
			.setCancelable(false)
			.setPositiveButton("Ja", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int id) {
					// Notifiera servern att vi g�r offline
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
		if (v == connectionButton){
			Toast.makeText(getBaseContext(), extras.get("connectionStatus").toString(), Toast.LENGTH_LONG).show();
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
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
		DatabaseController.db.close();
	}
	
	public void update(Observable observable, final Object data) {
		runOnUiThread(new Runnable(){
			public void run(){
				if(data != null && data instanceof Message)
					Toast.makeText(getApplicationContext(), "Meddelande fr�n "+
							((Message)data).getSrcUser()
							,Toast.LENGTH_LONG).show();
				
				if(data == ConnectionStatus.CONNECTED){
					connectionButton.setImageResource(R.drawable.connected);
					Toast.makeText(getApplicationContext(), "Ansluten till servern",Toast.LENGTH_LONG).show();
				}else if (data == ConnectionStatus.DISCONNECTED){
					connectionButton.setImageResource(R.drawable.disconnected);
					Toast.makeText(getApplicationContext(), "Tappad anslutning mot servern",Toast.LENGTH_LONG).show();
				}
				
				if (data instanceof GeoPoint){

				}
				
				if (data instanceof String){
					Toast.makeText(getBaseContext(), (String)data, Toast.LENGTH_SHORT).show();
				}
			}
		});

	}
}