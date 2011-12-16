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
	// inte f�rsvinner bara f�r att man l�mnar vyn tillf�lligt
	private static String txt = "";
	
	private Button button;
	private Button clear;
	private EditText et;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_RIGHT_ICON);
		setContentView(R.layout.send_sos_message);
		SessionController.titleBar(this, " - Skicka SOS-meddelande", true);

		button = (Button) findViewById(R.id.sos_send_message_button);
		et = (EditText) findViewById(R.id.sos_meddelande_edittext);
		clear = (Button) findViewById(R.id.sos_rensa_meddelande);
		
		updateLabels();
		
		OnClickListener ocl = new OnClickListener() {
			public void onClick(View v) {
				if (v == button) {
					if (SOS_ALARM_IS_ACTIVE) {
						cancelAlarm();
					} else {
						startAlarm();
					}
					updateLabels();
				}
				if (v == clear) 
					et.setText(txt = "");
			}
		};
		
		button.setOnClickListener(ocl);
		clear.setOnClickListener(ocl);
	}
	
	private void startAlarm() {
		MainView.mapCont.setSavedSnippet(MainView.mapCont.getYou().getSnippet());
		txt = et.getText().toString();
		
		// Ta bort dig sj�lv fr�n kartan
		MainView.mapCont.removeObject(MainView.mapCont.getYou(), false);
		
		MainView.mapCont.getYou().setSOS(true);
		MainView.mapCont.getYou().setSnippet(txt);
		MainView.mapCont.getYou().setTitle(SessionController.getUser()+", ALARM");
		
		// L�gger till dig sj�lv p� kartan
		MainView.mapCont.add(MainView.mapCont.getYou(), false);
		
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
		// Ta bort dig sj�lv p� kartan
		MainView.mapCont.removeObject(MainView.mapCont.getYou(), false);
		MainView.mapCont.getYou().setSOS(false);
		MainView.mapCont.getYou().setSnippet(MainView.mapCont.getSavedSnippet());
		MainView.mapCont.getYou().setTitle(SessionController.getUser());
		
		// L�gg till dig sj�lv igen
		MainView.mapCont.add(MainView.mapCont.getYou(), false);
		
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
	 * beroende p� om ett larm redan �r aktivt
	 */
	private void updateLabels() {
		if (SOS_ALARM_IS_ACTIVE) {
			button.setText("Avbryt");
			et.setText(txt);
			et.setEnabled(false);
			clear.setEnabled(false);
			clear.setVisibility(4);
		} else {
			button.setText("Skicka");
			et.setEnabled(true);
			clear.setEnabled(true);
			clear.setVisibility(0);
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
		SessionController.getSessionController().updateConnectionImage();
	}
}
