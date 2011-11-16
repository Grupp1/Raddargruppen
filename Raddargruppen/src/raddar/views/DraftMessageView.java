package raddar.views;

import raddar.gruppen.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class DraftMessageView extends Activity implements OnClickListener{

	public void onCreate(Bundle savedInstanceState){

		super.onCreate(savedInstanceState);

		setContentView(R.layout.draft_message);
		Bundle extras = getIntent().getExtras();
		TextView draftMessageDestUser = (TextView)this.findViewById(R.id.draftMessageDestUser);
		TextView draftMessageDate = (TextView)this.findViewById(R.id.draftMessageDate);
		TextView draftMessageSubject =(TextView)this.findViewById(R.id.draftMessageSubject);

		TextView draftMessage =(TextView)this.findViewById(R.id.draftMessage);
		draftMessageDestUser.setText(extras.get("reciever").toString());
		draftMessageDate.setText(extras.get("date").toString());
		draftMessageSubject.setText(extras.get("subject").toString());
		draftMessage.setText("\n"+extras.get("data").toString());
	}

	public void onClick(View v) {
		
		
	}
	
}
