package raddar.views;

import raddar.gruppen.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;


public class MessageChoiceView extends Activity implements OnClickListener {

	private ImageButton newMessageButton;
	private ImageButton inboxButton;
	private ImageButton outboxButton;
	private ImageButton draftButton;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message_choice);
		
		newMessageButton = (ImageButton)this.findViewById(R.id.newMessageButton);
		newMessageButton.setOnClickListener(this);
		
		inboxButton = (ImageButton)this.findViewById(R.id.inboxButton);
		inboxButton.setOnClickListener(this);
		
		outboxButton = (ImageButton)this.findViewById(R.id.outboxButton);
		outboxButton.setOnClickListener(this);
		
		draftButton = (ImageButton)this.findViewById(R.id.draftButton);
		draftButton.setOnClickListener(this);
		
}

	public void onClick(View v) {
		
		if(v == newMessageButton){
			Intent nextIntent = new Intent(MessageChoiceView.this, SendMessageView.class);
			startActivity(nextIntent);
		}
		
		if(v == inboxButton){
			Intent nextIntent = new Intent(MessageChoiceView.this, InboxView.class);
			startActivity(nextIntent);
		}
		
		if(v == outboxButton){
			Intent nextIntent = new Intent(MessageChoiceView.this, OutBoxView.class);
			startActivity(nextIntent);
		}
		
		if(v == draftButton){
			Intent nextIntent = new Intent(MessageChoiceView.this, DraftView.class);
			startActivity(nextIntent);
		}
		
	}
}
