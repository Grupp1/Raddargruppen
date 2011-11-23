package raddar.views;

import java.util.Observable;
import java.util.Observer;

import raddar.controllers.SipController;
import raddar.enums.ConnectionStatus;
import raddar.enums.LoginResponse;
import raddar.gruppen.R;
import raddar.models.LoginManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class StartView extends Activity implements Observer {

	private Button loginButton;
	private EditText user;
	private EditText password;
	/**
	 * The progressbar that is shown when the client is attempting to log in
	 */
	private ProgressDialog dialog;

	/**
	 * Called when the activity is first created. Starts a new thread to log on
	 * when the user presses a button
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start);
		// Lite h�rdkodade testanv�ndare att testa med
//		LoginManager.cache("Borche", "hej123");
//		LoginManager.cache("Danne", "raddar");
//		LoginManager.cache("danan612","raddar");
		LoginManager.cache("Alice", "longshot");

		user = (EditText) this.findViewById(R.id.userText);
		password = (EditText) this.findViewById(R.id.passwordText);
		// Endast f�r l�ttare testning

		user.setText("Alice");
		password.setText("longshot");

		final LoginManager lm = new LoginManager();
		lm.addObserver(this);
		dialog = new ProgressDialog(this);
		dialog.setCancelable(false);
		dialog.setTitle("Loggar in...");

		loginButton = (Button) this.findViewById(R.id.okButton);
		loginButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				v.getContext().deleteDatabase(user.getText().toString());
				String[] sipDetails = new String[3];
				sipDetails[0] = user.getText().toString();
				sipDetails[1] = password.getText().toString();
				sipDetails[2] = "ekiga.net";
				SipController.setSipDetails(sipDetails);
				//Intent nextIntent = new Intent(StartView.this, MainView.class);
				//nextIntent.putExtra("user", user.getText().toString());
				//startActivity(nextIntent);
				loginButton.setEnabled(false);
				dialog.show();

				Thread s = new Thread(new Runnable(){
					public void run() {
						lm.evaluate(user.getText().toString(),
								password.getText().toString());
					}
				});
				s.start();
			}
		});

	}

	public void onRestart() {
		super.onRestart();
		finish();

	}

	/**
	 * Called when the login manager is done checking if our password is correct
	 */
	public void update(Observable observable, final Object data) {

		runOnUiThread(new Runnable() {
			public void run() {
				if ((LoginResponse) data == LoginResponse.ACCEPTED) {
					Intent nextIntent = new Intent(StartView.this,
							MainView.class);
					nextIntent.putExtra("user", user.getText().toString());
					nextIntent.putExtra("connectionStatus", ConnectionStatus.CONNECTED);
					startActivity(nextIntent);

				} else if ((LoginResponse) data == LoginResponse.NO_SUCH_USER_OR_PASSWORD)
					Toast.makeText(StartView.this,
							"Ogiltigt anv�ndarnamn eller l�senord",
							Toast.LENGTH_LONG).show();
				else if ((LoginResponse) data == LoginResponse.NO_CONNECTION)
					Toast.makeText(StartView.this, "Ingen kontakt med servern",
							Toast.LENGTH_LONG).show();
				else if ((LoginResponse) data == LoginResponse.ACCEPTED_NO_CONNECTION) {
					Toast.makeText(StartView.this,
							"Ingen kontakt med servern, du loggas in lokalt",
							Toast.LENGTH_LONG).show();
					Intent nextIntent = new Intent(StartView.this,
							MainView.class);
					nextIntent.putExtra("user", user.getText().toString());
					nextIntent.putExtra("connectionStatus", ConnectionStatus.DISCONNECTED);
					startActivity(nextIntent);
				}
				loginButton.setEnabled(true);
				dialog.cancel();
			}
		});

	}
}
