package raddar.views;

import raddar.enums.LoginResponse;

import raddar.controllers.InternalComManager;

import raddar.gruppen.R;
import raddar.models.Login;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
		// Lite hårdkodade testanvändare att testa med
		Login.cache("Borche", "hej123");
		Login.cache("Danne", "raddar");
		Login.cache("Alice", "longshot");
		//Endast för lättare testning
		this.deleteDatabase("Alice");

		user = (EditText)this.findViewById(R.id.userText);
		password = (EditText)this.findViewById(R.id.passwordText);
		//Endast för lättare testning
		user.setText("Alice");
		password.setText("longshot");
		
		loginButton = (Button)this.findViewById(R.id.okButton);
		loginButton.setOnClickListener(new OnClickListener(){

			public void onClick(View v) { 
			
				if (Login.checkPassword(user.getText().toString(), password.getText().toString()) == LoginResponse.ACCEPTED) {
					Intent nextIntent = new Intent(StartView.this, MainView.class);
					nextIntent.putExtra("user",user.getText().toString());
					startActivity(nextIntent);
				} else {
					Toast.makeText(StartView.this, "Wrong username or password", Toast.LENGTH_LONG).show();
				}
			}

		});
	}
	public void onRestart(){
		super.onRestart();
		finish();
	}
}
