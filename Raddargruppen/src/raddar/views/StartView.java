package raddar.views;

import raddar.controllers.InternalComManager;
import raddar.gruppen.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class StartView extends Activity {

	private Button loginButton;
	private EditText user;
	private EditText password;
	InternalComManager controller;


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start);

		user = (EditText)this.findViewById(R.id.userText);
		password = (EditText)this.findViewById(R.id.passwordText);

		loginButton = (Button)this.findViewById(R.id.okButton);
		loginButton.setOnClickListener(new OnClickListener(){

			public void onClick(View v){ 
				
				Intent nextIntent = new Intent(StartView.this, MainView.class);
				startActivity(nextIntent);
			}

		});



	}
}
