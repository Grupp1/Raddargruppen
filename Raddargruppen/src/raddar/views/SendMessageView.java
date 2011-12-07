package raddar.views;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

import raddar.controllers.DatabaseController;
import raddar.controllers.Sender;
import raddar.controllers.SessionController;
import raddar.gruppen.R;
import raddar.models.Message;
import raddar.models.QoSManager;
import raddar.models.TextMessage;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * This class shows the view which allows users to send
 * 
 * @author danan612
 * 
 */

public class SendMessageView extends Activity implements OnClickListener {

	private EditText destUser;
	private EditText subject;
	private EditText messageData;
	private Button sendButton;
	private boolean isDraft = false;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_RIGHT_ICON);
		setContentView(R.layout.send_message);
		SessionController.titleBar(this, " - Nytt textmeddelande");

		destUser = (EditText) this.findViewById(R.id.destUser);
		subject = (EditText) this.findViewById(R.id.subject);
		messageData = (EditText) this.findViewById(R.id.messageData);
		sendButton = (Button) this.findViewById(R.id.sendButton);

		Bundle extras = getIntent().getExtras();
		try {

			//Utkast

			String[] items = (String[]) extras.getCharSequenceArray("message");

			//isDraft = extras.getBoolean("isDraft");

			isDraft = true;

			Log.d("destUser draft", items[0].toString());

			destUser.setText(items[0].toString());
			subject.setText(items[1].toString());
			messageData.setText(items[2].toString());

		} catch (Exception e) {
			Log.d("SendMessageView", "message:"+e.toString());

			try{

				// Kartan

				String destMapUser = extras.getString("map");

				isDraft = false;

				destUser.setText(destMapUser);

			} catch (Exception c){

				// Skicka meddelande

				isDraft = false;

				Log.d("SendMessageView", c.toString());
				destUser = (EditText) this.findViewById(R.id.destUser);
				subject = (EditText) this.findViewById(R.id.subject);
				messageData = (EditText) this.findViewById(R.id.messageData);
				sendButton = (Button) this.findViewById(R.id.sendButton);

			}
		}

		sendButton.setOnClickListener(this);
		destUser.setOnClickListener(this);
		destUser.setFocusable(false);

	}


	public void onClick(View v) {
		if (v.equals(sendButton)) {
			// Undersöker om alla fält är skrivna i
			// TODO: gör nogrannare undersökning
			String[] temp = new String[3];
			temp[0] = destUser.getText().toString().trim();
			temp[1] = subject.getText().toString().trim();
			temp[2] = messageData.getText().toString().trim();
			if (temp[0].equals("") || temp[1].equals("") || temp[2].equals("")) {
				Toast.makeText(getApplicationContext(), "Fyll i alla fält",
						Toast.LENGTH_SHORT).show();
				return;
			}
			sendMessages();

			Toast.makeText(getApplicationContext(), "Meddelande till "+destUser.getText().
					toString().trim(),
					Toast.LENGTH_SHORT).show();
			finish();
		} else if (v == destUser){
			Intent nextIntent = new Intent(SendMessageView.this, ContactView.class);
			startActivityForResult(nextIntent,0);

		}else{
			onBackPressed();
			Intent nextIntent = new Intent(SendMessageView.this,
					ContactView.class);
			startActivityForResult(nextIntent, 0);
			finish();

		}
	}

	private void sendMessages() {
		String[] destUsers = (destUser.getText().toString() + ";").split(";");
		Log.d("number of messages", destUsers.length + "");
		for (int i = 0; i < destUsers.length-1; i++) {
			Message m = new TextMessage(SessionController.getUser(), ""
					+ destUsers[i]);
			m.setSubject(subject.getText() + "");
			m.setData(messageData.getText() + "");
			m.setDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
			try {
				new Sender(m,
						InetAddress
						.getByName(raddar.enums.ServerInfo.SERVER_IP),
						raddar.enums.ServerInfo.SERVER_PORT);
				DatabaseController.db.addOutboxRow(m);
				DatabaseController.db.deleteDraftRow(m);

			} catch (UnknownHostException e) {
				DatabaseController.db.addDraftRow(m);
			}
		}
	}

	public void onBackPressed() {
		String[] destUsers = (destUser.getText().toString() + ";").split(";");
		Log.d("number of messages", destUsers.length + "");
		for (int i = 0; i < destUsers.length; i++) {
			Message m = new TextMessage(SessionController.getUser(), ""
					+ destUsers[i]);
			m.setSubject(subject.getText() + "");
			m.setData(messageData.getText() + "");
			if(!isDraft){
				Log.d("isDraft",m.getDestUser().toString());
				DatabaseController.db.addDraftRow(m);	

			}
		}

		super.onBackPressed();
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 0) {
			if (resultCode == RESULT_OK) {
				Bundle extras = data.getExtras();
				String temp = "";
				String[] destUsers = extras.getStringArray("contacts");

				for(int i = 0; i < destUsers.length; i++)
					temp += destUsers[i]+"; ";
				destUser.setText(temp);	

			}
		}
		else if (requestCode == 8) {
			Bundle extras = data.getExtras();
			String destU = extras.getString("destUser");
			destUser.setText(destU);

		}
		else if (requestCode == 7) {

		}
	}




	@Override
	public void onResume() {
		super.onResume();
		QoSManager.setCurrentActivity(this);
		QoSManager.setPowerMode();
	}

}

