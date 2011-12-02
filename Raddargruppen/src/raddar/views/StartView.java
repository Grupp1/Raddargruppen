package raddar.views;

import java.util.Observable;
import java.util.Observer;

import raddar.controllers.SessionController;
import raddar.controllers.SipController;
import raddar.enums.ConnectionStatus;
import raddar.enums.LoginResponse;
import raddar.gruppen.R;
import raddar.models.LoginManager;
import raddar.models.QoSManager;
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
	private boolean local = false;

	/**
	 * Called when the activity is first created. Starts a new thread to log on
	 * when the user presses a button
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start);
		SessionController.titleBar(this, " - Logga in");
		// Lite hårdkodade testanvändare att testa med
		LoginManager.cache("Borche", "hej123");
		LoginManager.cache("Danne", "raddar");
		LoginManager.cache("Alice", "longshot");
		LoginManager.cache("danan612","raddar");


		user = (EditText) this.findViewById(R.id.usertext);
		password = (EditText) this.findViewById(R.id.passwordtext);
		// Endast för lättare testning

		user.setText("danan612");
		password.setText("raddar");

		final LoginManager lm = new LoginManager();
		lm.addObserver(this);
		dialog = new ProgressDialog(this);
		dialog.setCancelable(false);
		dialog.setTitle("Loggar in...");

		loginButton = (Button) this.findViewById(R.id.okbutton);
		loginButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
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
					if (!local) {
						Intent nextIntent = new Intent(StartView.this,
								MainView.class);
						nextIntent.putExtra("user", user.getText().toString());
						nextIntent.putExtra("connectionStatus", ConnectionStatus.CONNECTED);
						
						startActivity(nextIntent);
						
					}
					Toast.makeText(StartView.this,
							"Ansluten till servern",
							Toast.LENGTH_LONG).show();
				} else if ((LoginResponse) data == LoginResponse.NO_SUCH_USER_OR_PASSWORD)
					Toast.makeText(StartView.this,
							"Ogiltigt användarnamn eller lösenord",
							Toast.LENGTH_LONG).show();
				else if ((LoginResponse) data == LoginResponse.NO_CONNECTION)
					Toast.makeText(StartView.this, "Ingen kontakt med servern",
							Toast.LENGTH_LONG).show();
				else if ((LoginResponse) data == LoginResponse.ACCEPTED_NO_CONNECTION) {
					local = true;
					Toast.makeText(StartView.this,
							"Ingen kontakt med servern, du loggas in lokalt",
							Toast.LENGTH_LONG).show();
					Intent nextIntent = new Intent(StartView.this,
							MainView.class);
					nextIntent.putExtra("user", user.getText().toString());
					nextIntent.putExtra("connectionStatus", ConnectionStatus.DISCONNECTED);
					startActivity(nextIntent);
				}
				else if((LoginResponse) data == LoginResponse.USER_ALREADY_LOGGED_IN){
					Toast.makeText(StartView.this, "Användaren är redan inloggad på servern",
							Toast.LENGTH_LONG).show();
				}
				loginButton.setEnabled(true);
				dialog.cancel();
			}
		});

	}
}
