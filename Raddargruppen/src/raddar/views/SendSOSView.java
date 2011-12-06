 package raddar.views;

import java.net.InetAddress;
import java.net.UnknownHostException;

import raddar.controllers.Sender;
import raddar.controllers.SessionController;
import raddar.enums.SOSType;
import raddar.enums.ServerInfo;
import raddar.models.QoSManager;
import raddar.models.SOSMessage;
import raddar.gruppen.R;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.maps.GeoPoint;

public class SendSOSView extends Activity {
	
	private static boolean SOS_ALARM_IS_ACTIVE = false;
	
	// statisk variabel som ser till att det man skrivit
	// inte försvinner bara för att man lämnar vyn tillfälligt
	private static String txt = "";
	
	private Button button;
	private Button clear;
	private EditText et;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_RIGHT_ICON);
		setContentView(R.layout.send_sos_message);
		SessionController.titleBar(this, " - Skicka SOS-meddelande");

		button = (Button) findViewById(R.id.sos_send_message_button);
		et = (EditText) findViewById(R.id.sos_meddelande_edittext);
		clear = (Button) findViewById(R.id.sos_rensa_meddelande);
		
		updateLabels();
		
		OnClickListener ocl = new OnClickListener() {
			public void onClick(View v) {
				if (v == button) {
					if (SOS_ALARM_IS_ACTIVE) {
						cancelAlarm();
						updateLabels();
					} else {
						startAlarm();
						updateLabels();
					}
				}
				if (v == clear) 
					et.setText(txt = "");
			}
		};
		
		button.setOnClickListener(ocl);
		clear.setOnClickListener(ocl);
	}
	
	private void startAlarm() {
		txt = et.getText().toString();
		GeoPoint point = MainView.mapCont.getYou().getPoint();
		SOSMessage sm = new SOSMessage(txt, SessionController.getUser(), SOSType.ALARM,
				point.getLatitudeE6(),point.getLongitudeE6());
		try {
			new Sender(sm, InetAddress.getByName(ServerInfo.SERVER_IP), ServerInfo.SERVER_PORT);
			SOS_ALARM_IS_ACTIVE = true;
		} catch (UnknownHostException e) {
			Log.d("BORCHE", "startAlarm() is SendSOSView.java");
		}
	}
	
	private void cancelAlarm() {
		txt = "";
		GeoPoint point = MainView.mapCont.getYou().getPoint();
		SOSMessage sm = new SOSMessage(SessionController.getUser(),"", SOSType.CANCEL_ALARM,
				point.getLatitudeE6(),point.getLongitudeE6());
		try {
			new Sender(sm, InetAddress.getByName(ServerInfo.SERVER_IP), ServerInfo.SERVER_PORT);
			SOS_ALARM_IS_ACTIVE = false;
		} catch (UnknownHostException e) {
			Log.d("BORCHE", "cancelAlarm() is SendSOSView.java");
		}
	}
	
	/*
	 * Knappens text ska skifta mellan Skicka och Avbryt, 
	 * beroende på om ett larm redan är aktivt
	 */
	private void updateLabels() {
		if (SOS_ALARM_IS_ACTIVE) {
			button.setText("Avbryt");
			et.setText(txt);
			et.setEnabled(false);
			clear.setEnabled(false);
		} else {
			button.setText("Skicka");
			et.setEnabled(true);
			clear.setEnabled(true);
			et.setText(txt);
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if ((keyCode == KeyEvent.KEYCODE_BACK)) {
	        txt = et.getText().toString();
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		QoSManager.setCurrentActivity(this);
		QoSManager.setPowerMode();
	}
}
