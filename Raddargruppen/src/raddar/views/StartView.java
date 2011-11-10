package raddar.views;


import raddar.controllers.InternalComManager;
import raddar.enums.LoginResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import raddar.controllers.InternalComManager;
import raddar.enums.NotificationType;
import raddar.enums.ServerInfo;

import raddar.gruppen.R;
import raddar.models.Login;
import raddar.models.NotificationMessage;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
		// Lite h�rdkodade testanv�ndare att testa med
		Login.cache("Borche", "hej123");
		Login.cache("Danne", "raddar");
		Login.cache("Alice", "longshot");
		//Endast f�r l�ttare testning
		this.deleteDatabase("Alice");

		user = (EditText)this.findViewById(R.id.userText);
		password = (EditText)this.findViewById(R.id.passwordText);
		//Endast f�r l�ttare testning
		user.setText("Alice");
		password.setText("longshot");
		
		loginButton = (Button)this.findViewById(R.id.okButton);
		loginButton.setOnClickListener(new OnClickListener(){

			public void onClick(View v) { 
			
				/*
				 * NotificationMessage inkapslar anv�ndarnamnet och l�senordet som
				 * anv�ndaren matar in vid inloggning. Meddelandet skickas sedan
				 * till servern...
				 */
				NotificationMessage nm = new NotificationMessage(user.getText().toString(), 
						NotificationType.CONNECT, 
						password.getText().toString());
				try {
					// Skapa socket som anv�nds f�r att skicka NotificationMessage
					Socket so = new Socket(InetAddress.getByName(ServerInfo.SERVER_IP), ServerInfo.SERVER_PORT);
					
					
					
					PrintWriter pw = new PrintWriter(so.getOutputStream(), true);
					pw.println(nm.getFormattedMessage());
					
					BufferedReader br = new BufferedReader(
							new InputStreamReader(so.getInputStream()));
					
					// L�s in ett svar fr�n servern via SAMMA socket
					String response = br.readLine();
					
					
					// Om servern s�ger att anv�ndarnamn och l�senord �r OK s� loggas man in 
					if (response.equalsIgnoreCase("OK")) {
						Intent nextIntent = new Intent(StartView.this, MainView.class);
						nextIntent.putExtra("user",user.getText().toString());
						startActivity(nextIntent);
					}
					//new Sender(nm, InetAddress.getByName("130.236.227.95"), 4043);
					
				} catch (IOException e) {
					Log.d("NotificationMessage", "Connect failed");
				}
				
				
				/* if (Login.checkPassword(user.getText().toString(), password.getText().toString()) == LoginResponse.ACCEPTED) {
					Intent nextIntent = new Intent(StartView.this, MainView.class);
					nextIntent.putExtra("user",user.getText().toString());
					startActivity(nextIntent);
				} else {
					Toast.makeText(StartView.this, "Fel anv�ndarnamn eller l�senord", Toast.LENGTH_LONG).show();
				} */
			}

		});
	}
	public void onRestart(){
		super.onRestart();
		finish();
	}
}
