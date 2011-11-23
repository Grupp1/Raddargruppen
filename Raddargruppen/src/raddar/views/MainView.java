package raddar.views;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Observable;
import java.util.Observer;

import raddar.controllers.DatabaseController;
import raddar.controllers.NotificationService;
import raddar.controllers.ReciveHandler;
import raddar.controllers.Sender;
import raddar.controllers.SessionController;
import raddar.controllers.SipController;
import raddar.enums.NotificationType;
import raddar.enums.RequestType;
import raddar.enums.ServerInfo;
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
		
		new SessionController(extras.get("user").toString());
		new DatabaseController(this);
		//new SipController(this);
		new ReciveHandler(this);
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
			//finish();
		}
		else if(v == setupButton){

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
		} catch (UnknownHostException e) {
			Log.d("NotificationMessage", "Disconnect failed");
		}
		DatabaseController.db.close();
	}
	
	public void update(Observable observable, final Object data) {
		runOnUiThread(new Runnable(){
			public void run(){	
				if(data != null && data instanceof Message){
					
					Toast.makeText(getApplicationContext(), "Meddelande från "+
							((Message)data).getSrcUser()
							,Toast.LENGTH_LONG).show();
				}
			}
		});

	}
}