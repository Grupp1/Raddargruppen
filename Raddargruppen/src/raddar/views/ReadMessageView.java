package raddar.views;

import raddar.controllers.SessionController;
import raddar.gruppen.R;
import raddar.models.QoSManager;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class ReadMessageView extends Activity{

	public void onCreate(Bundle savedInstanceState){

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_RIGHT_ICON);
		setContentView(R.layout.read_message);
		SessionController.titleBar(this, " - Meddelanden");
		Bundle extras = getIntent().getExtras();

		TextView readMessageSrcUser = (TextView)this.findViewById(R.id.readMessageSrcUser);
		TextView readMessageDate = (TextView)this.findViewById(R.id.readMessageDate);
		TextView readMessageSubject =(TextView)this.findViewById(R.id.readMessageSubject);

		TextView readMessage =(TextView)this.findViewById(R.id.readMessage);
		readMessageSrcUser.setText(extras.get("sender").toString());
		readMessageDate.setText(extras.get("date").toString());
		readMessageSubject.setText(extras.get("subject").toString());
		
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
