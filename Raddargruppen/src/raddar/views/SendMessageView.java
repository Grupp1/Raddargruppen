package raddar.views;

import java.net.InetAddress;
import java.net.UnknownHostException;

import raddar.controllers.InternalComManager;
import raddar.controllers.Sender;
import raddar.gruppen.R;
import raddar.models.Message;
import raddar.models.TextMessage;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
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
		destUser = (EditText) this.findViewById(R.id.destUser);
		subject = (EditText) this.findViewById(R.id.subject);
		messageData = (EditText) this.findViewById(R.id.messageData);
		sendButton = (Button) this.findViewById(R.id.sendButton);
		sendButton.setOnClickListener(this);
		destUser.setOnClickListener(this);
	}

	public void onClick(View v) {
		if (v.equals(sendButton)) {
			//Undersöker om alla fält är skrivna i
			//TODO: gör nogrannare undersökning
			String[] temp = new String[3];
			temp[0] = destUser.getText().toString().trim();
			temp[1] = subject.getText().toString().trim();
			temp[2] = messageData.getText().toString().trim();
			if (temp[0].equals("")
					|| temp[1].equals("")
					|| temp[2].equals("")) {
				Toast.makeText(getApplicationContext(), "Fyll i alla fält",
						Toast.LENGTH_SHORT).show();
				return;
			}
			Message m = new TextMessage(MainView.controller.getUser(), ""
					+ destUser.getText());
			m.setSubject(subject.getText() + "");
			m.setData(messageData.getText() + "");
			try {
				new Sender(m, InetAddress.getByName("130.236.227.95"), 4043);
			} catch (UnknownHostException e) {

			}
			Toast.makeText(getApplicationContext(), "Meddelande till "+destUser.getText().toString(),
					Toast.LENGTH_SHORT).show();
			finish();
		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(
					"Vill du lägga till en användare från kontaktlistan?")
					.setCancelable(false)
					.setPositiveButton("Ja",
							new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int id) {
							SendMessageView.this.addUserFromList();
						}
					})
					.setNegativeButton("Nej",
							new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int id) {
							dialog.cancel();
						}
					});
			AlertDialog alert = builder.create();
			alert.show();
		}

	}

	private void addUserFromList() {
		final CharSequence[] items = {  "Borche", "Daniel", "Thomas" };

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Välj en användare");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				Toast.makeText(getApplicationContext(), items[item],
						Toast.LENGTH_SHORT).show();
				destUser.setText(items[item]);
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
}
