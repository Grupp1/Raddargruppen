package raddar.views;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Observable;
import java.util.Observer;

import raddar.controllers.InternalComManager;
import raddar.controllers.ReciveHandler;
import raddar.controllers.Sender;
import raddar.enums.NotificationType;
import raddar.gruppen.R;
import raddar.models.ClientDatabaseManager;
import raddar.models.Inbox;
import raddar.models.Message;
import raddar.models.NotificationMessage;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainView extends Activity implements OnClickListener, Observer{

	private ImageButton callButton;
	private ImageButton messageButton;
	private ImageButton mapButton;
	private ImageButton reportButton;
	private ImageButton serviceButton;
	private ImageButton sosButton;
	private ImageButton setupButton;
	private ImageButton logButton;
	//H�ller reda p� interna kommunikationen p� servern. I dagsl�get
	//h�ller den endast reda p� vilken anv�ndare som �r online
	public static InternalComManager controller; 
	//Pekare p� databasen. Ska anv�ndas f�r att komma �t databasen
	public static ClientDatabaseManager db;


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Bundle extras = getIntent().getExtras();
		controller = new InternalComManager();
		controller.setUser(extras.get("user").toString());
		db = new ClientDatabaseManager(this,controller.getUser());
		new ReciveHandler();

		// Notifiera servern att vi kommer online
		
		NotificationMessage nm = new NotificationMessage(MainView.controller.getUser(), NotificationType.CONNECT);
		try {
			// �ndra localhost till serverns address n�r den
			// �r fastst�lld och portarna har �ppnats i projektrummet
			new Sender(nm, InetAddress.getByName("130.236.227.95"), 4043);	
		} catch (UnknownHostException e) {
			Log.d("NotificationMessage", "Connect failed");
		}
		 
		db.addObserver(this);

		callButton = (ImageButton)this.findViewById(R.id.callButton);
		callButton.setOnClickListener(this);

		messageButton = (ImageButton)this.findViewById(R.id.messageButton);
		messageButton.setOnClickListener(this);

		mapButton = (ImageButton)this.findViewById(R.id.mapButton);
		mapButton.setOnClickListener(this);

		reportButton = (ImageButton)this.findViewById(R.id.reportButton);
		reportButton.setOnClickListener(this);

		serviceButton = (ImageButton)this.findViewById(R.id.serviceButton);
		serviceButton.setOnClickListener(this);

		sosButton = (ImageButton)this.findViewById(R.id.sosButton);
		sosButton.setOnClickListener(this);

		setupButton = (ImageButton)this.findViewById(R.id.setupButton);
		setupButton.setOnClickListener(this);

		logButton = (ImageButton)this.findViewById(R.id.logButton);
		logButton.setOnClickListener(this);

	}

	public void onClick(View v) {

		if(v == callButton){
			//finish();
		}
		if(v == messageButton){
			Intent nextIntent = new Intent(MainView.this, MessageChoiceView.class);
			startActivity(nextIntent);
		}
		if(v == mapButton){
			Intent nextIntent = new Intent(MainView.this, MapUI.class);
			startActivity(nextIntent);
		}
		if(v == reportButton){
			//finish();
		}
		if(v == serviceButton){
			Intent nextIntent = new Intent(MainView.this, ServiceView.class);
			startActivity(nextIntent);
		}
		if(v == sosButton){
			//finish();
		}
		if(v == setupButton){
			Intent nextIntent = new Intent(MainView.this, AddContactView.class);
			startActivity(nextIntent);
		}
		if(v == logButton){
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
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// Notifiera servern att vi g�r offline
		
		NotificationMessage nm = new NotificationMessage(MainView.controller.getUser(), NotificationType.DISCONNECT);
		try {
			// �ndra localhost till serverns address n�r den
			// �r fastst�lld och portarna har �ppnats i projektrummet
			new Sender(nm, InetAddress.getByName("130.236.227.95"), 4043);		
		} catch (UnknownHostException e) {
			Log.d("NotificationMessage", "Disconnect failed");
		}
		 
	}

	public void update(Observable observable, final Object data) {
		runOnUiThread(new Runnable(){
			public void run(){	
				if(data != null)
					Toast.makeText(getApplicationContext(), "Meddelande fr�n "+
							((Message)data).getSrcUser()
							,Toast.LENGTH_LONG).show();
			}
		});

	}
}