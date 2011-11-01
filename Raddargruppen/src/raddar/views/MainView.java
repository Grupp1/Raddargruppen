package raddar.views;

import raddar.gruppen.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;

public class MainView extends Activity implements OnClickListener {

	private ImageButton callButton;
	private ImageButton messageButton;
	private ImageButton mapButton;
	private ImageButton reportButton;
	private ImageButton sosButton;
	private ImageButton setupButton;
	private Button logButton;
	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		callButton = (ImageButton)this.findViewById(R.id.callButton);
		callButton.setOnClickListener(this);

		messageButton = (ImageButton)this.findViewById(R.id.messageButton);
		messageButton.setOnClickListener(this);

		mapButton = (ImageButton)this.findViewById(R.id.mapButton);
		mapButton.setOnClickListener(this);

		reportButton = (ImageButton)this.findViewById(R.id.reportButton);
		reportButton.setOnClickListener(this);

		sosButton = (ImageButton)this.findViewById(R.id.sosButton);
		sosButton.setOnClickListener(this);

		setupButton = (ImageButton)this.findViewById(R.id.setupButton);
		setupButton.setOnClickListener(this);

		logButton = (Button)this.findViewById(R.id.logButton);
		logButton.setOnClickListener(this);

	}

	public void onClick(View v) {

		if(v == callButton){
			finish();
		}
		if(v == messageButton){
			Intent nextIntent = new Intent(MainView.this, InboxView.class);
			startActivity(nextIntent);
		}
		if(v == mapButton){
			Intent nextIntent = new Intent(MainView.this, Map.class);
			startActivity(nextIntent);
		}
		if(v == reportButton){
			finish();
		}
		if(v == sosButton){
			finish();
		}
		if(v == setupButton){
			finish();
		}
		if(v == logButton){
			finish();
		}




	}
}