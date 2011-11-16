package raddar.views;

import raddar.gruppen.R;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class ReadMessageView extends Activity{
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.read_message);
		Bundle extras = getIntent().getExtras();
		TextView readMessageSrcUser = (TextView)this.findViewById(R.id.readMessageSrcUser);
		TextView readMessageDate = (TextView)this.findViewById(R.id.readMessageDate);
		TextView readMessageSubject =(TextView)this.findViewById(R.id.readMessageSubject);

		TextView readMessage =(TextView)this.findViewById(R.id.readMessage);
		readMessageSrcUser.setText(extras.get("sender").toString());
		readMessageDate.setText(extras.get("date").toString());
		readMessageSubject.setText(extras.get("subject").toString());
		readMessage.setText("\n"+extras.get("data").toString());
	}

}
