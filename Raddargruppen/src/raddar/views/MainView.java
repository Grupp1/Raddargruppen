package raddar.views;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Observable;
import java.util.Observer;

import raddar.controllers.InternalComManager;
import raddar.controllers.ReciveHandler;
import raddar.controllers.Sender;
import raddar.enums.NotificationType;
import raddar.enums.ServerInfo;
import raddar.gruppen.R;
import raddar.models.ClientDatabaseManager;
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
	public static InternalComManager controller; 
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
			finish();
		}
		if(v == messageButton){
			Intent nextIntent = new Intent(MainView.this, MessageChoiceView.class);
			startActivity(nextIntent);
		}
		if(v == mapButton){
			Intent nextIntent = new Intent(MainView.this, Map.class);
			startActivity(nextIntent);
		}
		if(v == reportButton){
			finish();
		}
		if(v == serviceButton){
			finish();
		}
		if(v == sosButton){
			finish();
		}
		if(v == setupButton){
			finish();
		}
		if(v == logButton){
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Är du säker på att du vill logga ut?")
			.setCancelable(false)
			.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int id) {
					// Notifiera servern att vi går offline
					NotificationMessage nm = new NotificationMessage(MainView.controller.getUser(), 
							NotificationType.DISCONNECT);
					try {
						// Skicka meddelandet
						new Sender(nm, InetAddress.getByName(ServerInfo.SERVER_IP), ServerInfo.SERVER_PORT);		
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
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// Notifiera servern att vi går offline
		NotificationMessage nm = new NotificationMessage(MainView.controller.getUser(), 
				NotificationType.DISCONNECT);
		try {
			// Skicka meddelandet
			new Sender(nm, InetAddress.getByName(ServerInfo.SERVER_IP), ServerInfo.SERVER_PORT);		
		} catch (UnknownHostException e) {
			Log.d("NotificationMessage", "Disconnect failed");
		}
		 
	}

	public void update(Observable observable, final Object data) {
		runOnUiThread(new Runnable(){
			public void run(){	
				if(data != null)
					Toast.makeText(getApplicationContext(), "Meddelande från "+
							((Message)data).getSrcUser()
							,Toast.LENGTH_LONG).show();
			}
		});

	}
}