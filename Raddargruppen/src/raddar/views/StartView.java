package raddar.views;

import raddar.controllers.InternalComManager;
import raddar.gruppen.R;
import raddar.models.LoginManager;
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
		// Lite h�rdkodade testanv�ndare att testa med
		LoginManager.cache("Borche", "hej123");
		LoginManager.cache("Danne", "raddar");
		LoginManager.cache("Alice", "longshot");
		// Endast f�r l�ttare testning
		this.deleteDatabase("Alice");

		user = (EditText) this.findViewById(R.id.userText);
		password = (EditText) this.findViewById(R.id.passwordText);
		// Endast f�r l�ttare testning
		user.setText("Alice");
		password.setText("longshot");

		loginButton = (Button) this.findViewById(R.id.okButton);
		loginButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				boolean valid = LoginManager.evaluate(MainView.controller
						.getUser(), password.getText().toString());

				if (valid) {
					Intent nextIntent = new Intent(StartView.this,
							MainView.class);
					nextIntent.putExtra("user", user.getText().toString());
					startActivity(nextIntent);
				} else
					Toast.makeText(StartView.this,
							"Ogiltigt anv�ndarnamn eller l�senord",
							Toast.LENGTH_LONG).show();
			}
		});
	}

	public void onRestart() {
		super.onRestart();
		finish();
	}
}
