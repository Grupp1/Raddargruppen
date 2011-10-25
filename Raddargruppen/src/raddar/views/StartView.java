package raddar.views;

import java.io.Serializable;

import raddar.enums.MessageType;
import raddar.gruppen.R;
import raddar.models.TextMessage;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class StartView extends Activity implements Serializable {

	private Button loginButton;
	private EditText user;
	private EditText password;
	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start);

		//onStart();
		loginButton = (Button)this.findViewById(R.id.okButton);
		//loginButton.setOnClickListener(this);
		user = (EditText)this.findViewById(R.id.userText);
		password = (EditText)this.findViewById(R.id.passwordText);
		
		loginButton.setOnClickListener(new OnClickListener(){
			public void onClick(View v){ 
				Intent nextIntent = new Intent(StartView.this, MainView.class);
				startActivity(nextIntent);
			}
		});
	}

//	public void onClick(View v) {
		
		
		//setContentView(R.layout.main);
		
		//	Intent nextIntent = new Intent(StartView.this, MainView.class);
		//	startActivity(nextIntent);
		

	//}
	//anropar loginfunktionen - går vidare till main

}


