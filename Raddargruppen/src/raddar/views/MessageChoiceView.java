package raddar.views;

import raddar.controllers.SessionController;
import raddar.gruppen.R;
import raddar.models.QoSManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;


public class MessageChoiceView extends Activity implements OnClickListener {

	private ImageButton newTextButton;
	private ImageButton newPictureButton;
	private ImageButton inboxButton;
	private ImageButton outboxButton;
	private ImageButton draftButton;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_RIGHT_ICON);
		setContentView(R.layout.message_choice);
		SessionController.titleBar(this, " - Meddelanden");
		
		newTextButton = (ImageButton)this.findViewById(R.id.newTextButton);
		newTextButton.setOnClickListener(this);
		
		newPictureButton = (ImageButton)this.findViewById(R.id.newPictureButton);
		newPictureButton.setOnClickListener(this);
		
		inboxButton = (ImageButton)this.findViewById(R.id.inboxButton);
		inboxButton.setOnClickListener(this);
		
		outboxButton = (ImageButton)this.findViewById(R.id.outboxButton);
		outboxButton.setOnClickListener(this);
		
		draftButton = (ImageButton)this.findViewById(R.id.draftButton);
		draftButton.setOnClickListener(this);
		
}

	public void onClick(View v) {
		
		if(v == newTextButton){
			Intent nextIntent = new Intent(MessageChoiceView.this, SendMessageView.class);
			startActivity(nextIntent);
		}
		
		if(v == newPictureButton){
			Intent nextIntent = new Intent(MessageChoiceView.this, SendImageMessageView.class);
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
	
	@Override
	public void onResume() {
		super.onResume();
		QoSManager.setCurrentActivity(this);
		QoSManager.setPowerMode();
		SessionController.getSessionController().updateConnectionImage();
	}
	
}
