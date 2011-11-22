package raddar.views;

import java.net.InetAddress;
import java.net.UnknownHostException;

import raddar.controllers.DatabaseController;
import raddar.controllers.Sender;
import raddar.controllers.SessionController;
import raddar.gruppen.R;
import raddar.models.Message;
import raddar.models.TextMessage;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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


	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		

		setContentView(R.layout.send_message);

		try{

			Bundle extras = getIntent().getExtras();
			new SessionController(extras.get("user").toString());
			String [] items = (String[]) extras.get("message");

			destUser = (EditText) this.findViewById(R.id.destUser);
			subject = (EditText) this.findViewById(R.id.subject);
			messageData = (EditText) this.findViewById(R.id.messageData);

			destUser.setText(items[0]);
			subject.setText(items[1]);
			messageData.setText(items[2]);
			
			sendButton = (Button) this.findViewById(R.id.sendButton);
			sendButton.setOnClickListener(this);
			destUser.setOnClickListener(this);

		}catch(Exception e){
			
			destUser = (EditText) this.findViewById(R.id.destUser);
			subject = (EditText) this.findViewById(R.id.subject);
			messageData = (EditText) this.findViewById(R.id.messageData);
			sendButton = (Button) this.findViewById(R.id.sendButton);
			sendButton.setOnClickListener(this);
			destUser.setOnClickListener(this);
		
		}
	}

	public void onClick(View v) {
		if (v.equals(sendButton)) {
			//Unders�ker om alla f�lt �r skrivna i
			//TODO: g�r nogrannare unders�kning
			String[] temp = new String[3];
			temp[0] = destUser.getText().toString().trim();
			temp[1] = subject.getText().toString().trim();
			temp[2] = messageData.getText().toString().trim();
			if (temp[0].equals("")
					|| temp[1].equals("")
					|| temp[2].equals("")) {
				Toast.makeText(getApplicationContext(), "Fyll i alla f�lt",
						Toast.LENGTH_SHORT).show();
				return;
			}
			
			sendMessages();
			
			/*
			Message m = new TextMessage(MainView.controller.getUser(), ""
					+ destUser.getText());
			m.setSubject(subject.getText() + "");
			m.setData(messageData.getText() + "");
			try {
				new Sender(m, InetAddress.getByName(ServerInfo.SERVER_IP), ServerInfo.SERVER_PORT);
			} catch (UnknownHostException e) {

			}
			 */
			//			Toast.makeText(getApplicationContext(), "Meddelande till "+destUser.getText().
			//					toString().trim(),
			//					Toast.LENGTH_SHORT).show();
			Intent nextIntent = new Intent(SendMessageView.this, MessageChoiceView.class);
			startActivity(nextIntent);
			finish();
		}		
		else {
			onBackPressed();
			Intent nextIntent = new Intent(SendMessageView.this, ContactView.class);
			startActivityForResult(nextIntent,0);

			finish();
		}
	}


	private void sendMessages(){
		String[] destUsers = (destUser.getText().toString()+";").split(";");
		Log.d("number of messages",destUsers.length+"");
		for(int i = 0; i < destUsers.length;i++){
			Message m = new TextMessage(SessionController.getUser(), ""
					+ destUsers[i]);
			m.setSubject(subject.getText() + "");
			m.setData(messageData.getText() + "");
			try {
				new Sender(m, InetAddress.getByName(raddar.enums.ServerInfo.SERVER_IP), raddar.enums.ServerInfo.SERVER_PORT);
				DatabaseController.db.addOutboxRow(m);
				DatabaseController.db.deleteDraftRow(m);

			} catch (UnknownHostException e) {
				DatabaseController.db.addDraftRow(m);
			}
		}
	}

	public void onBackPressed() {
		String[] destUsers = (destUser.getText().toString()+";").split(";");
		Log.d("number of messages",destUsers.length+"");
		for(int i = 0; i < destUsers.length;i++){
			Message m = new TextMessage(SessionController.getUser(), ""
					+ destUsers[i]);
			m.setSubject(subject.getText() + "");
			m.setData(messageData.getText() + "");
			DatabaseController.db.addDraftRow(m);
		}

		super.onBackPressed();
	}


	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == 0){
			if(resultCode == RESULT_OK){
				Bundle extras = data.getExtras();
				String temp = "";
				String[] destUsers = extras.getStringArray("contacts");
				for(int i = 0; i < destUsers.length; i++)
					temp += destUsers[i]+";";
				destUser.setText(temp);	
			}
		}
	}
}
