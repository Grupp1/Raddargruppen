package raddar.views;

import raddar.gruppen.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class AcceptCallView extends Activity implements OnClickListener{
	private Button acceptCall;
	private Button denyCall;
	private TextView callerText;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.accept_call);

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

}
