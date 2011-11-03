package raddar.views;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Observable;
import java.util.Observer;

import raddar.controllers.InternalComManager;
import raddar.controllers.Sender;
import raddar.enums.NotificationType;
import raddar.gruppen.R;
import raddar.models.Inbox;
import raddar.models.NotificationMessage;
import android.app.Activity;
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
	private ImageButton sosButton;
	private ImageButton setupButton;
	private Button logButton;
	public static InternalComManager controller = new InternalComManager();


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Notifiera servern att vi kommer online
		/* 
		NotificationMessage nm = new NotificationMessage("username", NotificationType.CONNECT);
		try {
			// Ändra localhost till serverns address när den
			// är fastställd och portarna har öppnats i projektrummet
			new Sender(nm, InetAddress.getLocalHost(), 6789);	
		} catch (UnknownHostException e) {
			Log.d("NotificationMessage", "Connect failed");
		}
		 */
		controller.addObserverToInbox(this);

		callButton = (ImageButton)this.findViewById(R.id.callButton);
		callButton.setOnClickListener(this);

		messageButton = (ImageButton)this.findViewById(R.id.messageButton);
		messageButton.setOnClickListener(this);

		mapButton = (ImageButton)this.findViewById(R.id.mapButton);
		mapButton.setOnClickListener(this);

		reportButton = (ImageButton)this.findViewById(R.id.reportButton);
		reportButton.setOnClickListener(this);

		sosButton = (ImageButton)this.findViewById(R.id.sosButton);
		sosButton.setOnClickListener(this);

		setupButton = (ImageButton)this.findViewById(R.id.setupButton);
		setupButton.setOnClickListener(this);

		logButton = (Button)this.findViewById(R.id.logButton);
		logButton.setOnClickListener(this);

	}

	public void onClick(View v) {

		if(v == callButton){
			finish();
		}
		if(v == messageButton){
			Intent nextIntent = new Intent(MainView.this, InboxView.class);
			startActivity(nextIntent);
		}
		if(v == mapButton){
			Intent nextIntent = new Intent(MainView.this, Map.class);
			startActivity(nextIntent);
		}
		if(v == reportButton){
			finish();
		}
		if(v == sosButton){
			Intent nextIntent = new Intent(MainView.this, SendMessageView.class);
			startActivity(nextIntent);
		}
		if(v == setupButton){
			finish();
		}
		if(v == logButton){
			finish();
		}




	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// Notifiera servern att vi går offline
		/* 
		NotificationMessage nm = new NotificationMessage("username", NotificationType.DISCONNECT);
		try {
			// Ändra localhost till serverns address när den
			// är fastställd och portarna har öppnats i projektrummet
			new Sender(nm, InetAddress.getLocalHost(), 6789);	
		} catch (UnknownHostException e) {
			Log.d("NotificationMessage", "Disconnect failed");
		}
		 */
	}

	public void update(Observable observable, final Object data) {
		runOnUiThread(new Runnable(){
			public void run(){			
				if(data != null)
					Toast.makeText(getApplicationContext(), "Meddelande från "+data.toString()
							,Toast.LENGTH_LONG).show();
			}
		});

	}
}