package raddar.views;

import raddar.controllers.SessionController;
import raddar.gruppen.R;
import raddar.models.QoSManager;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class AcceptCallView extends Activity implements OnClickListener{
	private Button acceptCall;
	private Button denyCall;
	private TextView callerText;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_RIGHT_ICON);
		setContentView(R.layout.walkietalkie);
		SessionController.titleBar(this, " - Samtal", true);

		acceptCall = (Button)this.findViewById(R.id.denyCall);
		acceptCall.setOnClickListener(this);

		denyCall = (Button)this.findViewById(R.id.denyCall);
		denyCall.setOnClickListener(this);

		callerText = (TextView)this.findViewById(R.id.callerText);  //d�r namnet p� den som ringer ska in
		
	}

	public void onClick(View v) { 
		if(v == acceptCall){

		}
		if(v == denyCall){

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
