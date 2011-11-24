package raddar.views;

import java.net.InetAddress;
import java.net.UnknownHostException;

import raddar.controllers.Sender;
import raddar.controllers.SessionController;
import raddar.enums.SOSType;
import raddar.enums.ServerInfo;
import raddar.gruppen.R;
import raddar.models.SOSMessage;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class SendSOSView extends Activity {
	
	private static boolean SOS_ALARM_IS_ACTIVE = false;
	
	private static String txt = "";
	
	private Button button;
	private EditText et;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.send_sos_message);

		button = (Button) findViewById(R.id.sos_send_message_button);
		et = (EditText) findViewById(R.id.sos_meddelande_edittext);
		
		updateLabels();
		
		button.setOnClickListener(new OnClickListener() {
			@Override
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
			}
		});
	}
	
	private void startAlarm() {
		txt = et.getText().toString();
		SOSMessage sm = new SOSMessage(txt, SessionController.getUser(), SOSType.ALARM);
		try {
			new Sender(sm, InetAddress.getByName(ServerInfo.SERVER_IP), ServerInfo.SERVER_PORT);
			SOS_ALARM_IS_ACTIVE = true;
		} catch (UnknownHostException e) {
			Log.d("BORCHE", "startAlarm() is SendSOSView.java");
		}
	}
	
	private void cancelAlarm() {
		txt = "";
		SOSMessage sm = new SOSMessage(SessionController.getUser(), SOSType.CANCEL_ALARM);
		try {
			new Sender(sm, InetAddress.getByName(ServerInfo.SERVER_IP), ServerInfo.SERVER_PORT);
			SOS_ALARM_IS_ACTIVE = false;
		} catch (UnknownHostException e) {
			Log.d("BORCHE", "cancelAlarm() is SendSOSView.java");
		}
	}
	
	private void updateLabels() {
		if (SOS_ALARM_IS_ACTIVE) {
			button.setText("Avbryt");
			et.setText(txt);
			et.setEnabled(false);
		} else {
			button.setText("Send");
			et.setEnabled(true);
			et.setText(txt);
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		
	}

}
