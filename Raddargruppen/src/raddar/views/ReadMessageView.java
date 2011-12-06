package raddar.views;

import raddar.controllers.SessionController;
import raddar.gruppen.R;
import raddar.models.QoSManager;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import android.view.Window;

import android.view.View;
import android.widget.Button;

import android.widget.ImageView;
import android.widget.TextView;

public class ReadMessageView extends Activity{

	public void onCreate(Bundle savedInstanceState){

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_RIGHT_ICON);
		setContentView(R.layout.read_message);
		final Bundle extras = getIntent().getExtras();
		SessionController.titleBar(this, " - Meddelanden");

		TextView readMessageSrcUser = (TextView)this.findViewById(R.id.readMessageSrcUser);
		TextView readMessageDate = (TextView)this.findViewById(R.id.readMessageDate);
		TextView readMessageSubject =(TextView)this.findViewById(R.id.readMessageSubject);
		Button answerButton = (Button)this.findViewById(R.id.answerButton);

		TextView readMessage =(TextView)this.findViewById(R.id.readMessage);
		readMessageSrcUser.setText(extras.get("sender").toString());
		readMessageDate.setText(extras.get("date").toString());
		readMessageSubject.setText(extras.get("subject").toString());
		answerButton.setText("Svara");

		answerButton.setOnClickListener(new View.OnClickListener(){

			public void onClick(View v) {
				
				String [] items = {extras.get("sender").toString(), "",""};
				Intent nextIntent = new Intent(ReadMessageView.this, SendMessageView.class);
				nextIntent.putExtra("message", items);
				startActivity(nextIntent);

				finish();

			}
		});




		try{
			readMessage.setText("\n"+extras.get("data").toString()); //Om textbildmeddelande
		}
		catch(Exception e){
			ImageView readMessageImage = (ImageView)this.findViewById(R.id.image); // Om bildmeddelande
			readMessageImage.setImageBitmap((Bitmap)extras.get("image"));
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		QoSManager.setCurrentActivity(this);
		QoSManager.setPowerMode();
	}

}
