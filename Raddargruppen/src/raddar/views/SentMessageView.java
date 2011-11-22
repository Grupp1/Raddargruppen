package raddar.views;

import raddar.gruppen.R;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Denna klass ritar ut det skickade meddelandet som klickas på 
 * @author magkj501
 *
 */

public class SentMessageView extends Activity{

	public void onCreate(Bundle savedInstanceState){

		super.onCreate(savedInstanceState);

		setContentView(R.layout.sent_message);
		Bundle extras = getIntent().getExtras();
		TextView sentMessageDestUser = (TextView)this.findViewById(R.id.sentMessageDestUser);
		TextView sentMessageDate = (TextView)this.findViewById(R.id.sentMessageDate);
		TextView sentMessageSubject =(TextView)this.findViewById(R.id.sentMessageSubject);

		TextView sentMessage =(TextView)this.findViewById(R.id.sentMessage);
		sentMessageDestUser.setText(extras.get("reciever").toString());
		sentMessageDate.setText(extras.get("date").toString());
		sentMessageSubject.setText(extras.get("subject").toString());
		sentMessage.setText("\n"+extras.get("data").toString());
	}

}
