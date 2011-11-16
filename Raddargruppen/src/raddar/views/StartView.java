package raddar.views;

import java.util.Observable;
import java.util.Observer;

import raddar.enums.LoginResponse;
import raddar.gruppen.R;
import raddar.models.LoginManager;
import raddar.models.Message;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class StartView extends Activity implements Observer{

	private Button loginButton;
	private EditText user;
	private EditText password;
	private ProgressDialog dialog;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start);
		// Lite hårdkodade testanvändare att testa med
		LoginManager.cache("Borche", "hej123");
		LoginManager.cache("Danne", "raddar");
		LoginManager.cache("Alice", "longshot");
		// Endast för lättare testning
		this.deleteDatabase("Alice");

		user = (EditText) this.findViewById(R.id.userText);
		password = (EditText) this.findViewById(R.id.passwordText);
		// Endast för lättare testning
		user.setText("Alice");
		password.setText("longshot");

		final LoginManager lm = new LoginManager();
		lm.addObserver(this);
		dialog = new ProgressDialog(this);
		dialog.setCancelable(false);
		dialog.setTitle("Loggar in...");
		
		loginButton = (Button) this.findViewById(R.id.okButton);
		loginButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				loginButton.setEnabled(false);
				
				dialog.show();

				Thread s = new Thread(new Runnable(){
					public void run() {
						lm.evaluate(user.getText().toString(), password.getText().toString());
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

	public void update(Observable observable, final Object data) {
		runOnUiThread(new Runnable(){
			public void run(){	
				if ((LoginResponse)data == LoginResponse.ACCEPTED) {
					Intent nextIntent = new Intent(StartView.this,
							MainView.class);
					nextIntent.putExtra("user", user.getText().toString());
					startActivity(nextIntent);
					
				} else if((LoginResponse)data == LoginResponse.NO_SUCH_USER_OR_PASSWORD)
					Toast.makeText(StartView.this,
							"Ogiltigt användarnamn eller lösenord",
							Toast.LENGTH_LONG).show();
				else if((LoginResponse)data == LoginResponse.NO_CONNECTION)
					Toast.makeText(StartView.this,
							"Ingen kontakt med servern",
							Toast.LENGTH_LONG).show();
				else if((LoginResponse)data == LoginResponse.ACCEPTED_NO_CONNECTION){
					Toast.makeText(StartView.this,
							"Ingen kontakt med servern",
							Toast.LENGTH_LONG).show();
					Intent nextIntent = new Intent(StartView.this,
							MainView.class);
					nextIntent.putExtra("user", user.getText().toString());

					startActivity(nextIntent);
				}
				loginButton.setEnabled(true);
				dialog.cancel();
			}
		});

	}
}
