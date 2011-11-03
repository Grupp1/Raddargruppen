package raddar.views;

import java.net.InetAddress;
import java.net.UnknownHostException;

import raddar.controllers.InternalComManager;
import raddar.controllers.Sender;
import raddar.gruppen.R;
import raddar.models.Message;
import raddar.models.TextMessage;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class SendMessageView extends Activity implements OnClickListener{
	private EditText destUser;
	private EditText subject;
	private EditText messageData;
	private Button sendButton;
	private InternalComManager controller;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.send_message);
		destUser = (EditText)this.findViewById(R.id.destUser);
		subject = (EditText)this.findViewById(R.id.subject);
		messageData = (EditText)this.findViewById(R.id.messageData);
		sendButton = (Button)this.findViewById(R.id.sendButton);
		sendButton.setOnClickListener(this);		
	}

	public void onClick(View v) {
		Message m = new TextMessage("getUser()",""+destUser.getText());
		m.setSubject(subject.getText()+"");
		m.setData(messageData.getText()+"");
		try {
			new Sender (m, InetAddress.getByName("127.0.0.1"), 6789);
		} catch (UnknownHostException e) {
			
		}
		
	}
}
