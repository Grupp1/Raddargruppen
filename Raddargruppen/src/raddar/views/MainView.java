package raddar.views;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.Observable;
import java.util.Observer;

import raddar.controllers.ReciveHandler;
import raddar.controllers.Sender;
import raddar.controllers.SessionController;
import raddar.enums.NotificationType;
import raddar.enums.ServerInfo;
import raddar.gruppen.R;
import raddar.models.ClientDatabaseManager;
import raddar.models.Message;
import raddar.models.NotificationMessage;
import raddar.sip.IncomingCallReceiver;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.sip.SipException;
import android.net.sip.SipManager;
import android.net.sip.SipProfile;
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
	private ImageButton contactButton;
	private ImageButton serviceButton;
	private ImageButton sosButton;
	private ImageButton setupButton;
	private ImageButton logButton;
	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Bundle extras = getIntent().getExtras();
		
		new SessionController(this,extras.get("user").toString());
		new ReciveHandler();

		SessionController.db.addObserver(this);

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
		if(v == contactButton){
			Intent nextIntent = new Intent(MainView.this, ContactListView.class);
			startActivity(nextIntent);
		}
		if(v == serviceButton){
			Intent nextIntent = new Intent(MainView.this, ServiceView.class);
			startActivity(nextIntent);
		}
		if(v == sosButton){
			//finish();
		}
		if(v == setupButton){

		}
		if(v == logButton){
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
		SessionController.onDestroy();
		// Notifiera servern att vi går offline
		NotificationMessage nm = new NotificationMessage(SessionController.getUser(), 
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
				if(data != null && data instanceof Message)
					Toast.makeText(getApplicationContext(), "Meddelande från "+
							((Message)data).getSrcUser()
							,Toast.LENGTH_LONG).show();
			}
		});

	}
}