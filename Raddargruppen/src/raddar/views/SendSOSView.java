 package raddar.views;

import java.net.InetAddress;
import java.net.UnknownHostException;

import raddar.controllers.Sender;
import raddar.controllers.SessionController;
import raddar.enums.MapOperation;
import raddar.enums.ServerInfo;
import raddar.gruppen.R;
import raddar.models.MapObjectMessage;
import raddar.models.QoSManager;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;

public class SendSOSView extends Activity {
	
	private static boolean SOS_ALARM_IS_ACTIVE = false;
	
	// statisk variabel som ser till att det man skrivit
	// inte försvinner bara för att man lämnar vyn tillfälligt
	private static String txt = "";
	
	private Button button;
	private Button clear;
	private EditText et;
	private String savedSnippet;
	
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
		savedSnippet = MainView.mapCont.getYou().getSnippet();
		txt = et.getText().toString();
		MainView.mapCont.getYou().setSOS(true);
		MainView.mapCont.getYou().setSnippet(txt);
		MainView.mapCont.getYou().setTitle(SessionController.getUser()+", ALARM");
		Gson gson = new Gson();
		MapObjectMessage mo = new MapObjectMessage(gson.toJson(MainView.mapCont.getYou()),
				(MainView.mapCont.getYou()).getClass().getName(),MainView.mapCont.getYou().getId(),
				MapOperation.ALARM_ON);
		try {
			new Sender(mo, InetAddress.getByName(ServerInfo.SERVER_IP), ServerInfo.SERVER_PORT);
			SOS_ALARM_IS_ACTIVE = true;
		} catch (UnknownHostException e) {
			Log.d("SendSOS", "startAlarm() is SendSOSView.java");
		}
	}
	
	private void cancelAlarm() {
		txt = "";
		MainView.mapCont.getYou().setSOS(false);
		MainView.mapCont.getYou().setSnippet(savedSnippet);
		MainView.mapCont.getYou().setTitle(SessionController.getUser());
		Gson gson = new Gson();
		MapObjectMessage mo = new MapObjectMessage(gson.toJson(MainView.mapCont.getYou()),
				(MainView.mapCont.getYou()).getClass().getName(),MainView.mapCont.getYou().getId(),
				MapOperation.ALARM_OFF);
		
		try {
			new Sender(mo, InetAddress.getByName(ServerInfo.SERVER_IP), ServerInfo.SERVER_PORT);
			SOS_ALARM_IS_ACTIVE = false;
		} catch (UnknownHostException e) {
			Log.d("SendSOS", "cancelAlarm() is SendSOSView.java");
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
