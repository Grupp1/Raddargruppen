package raddar.views;

import java.io.IOException;
import java.net.InetAddress;

import raddar.controllers.ReciveHandler;
import raddar.controllers.Sender;
import raddar.enums.MessageType;
import raddar.gruppen.R;
import raddar.models.Message;
import raddar.models.TextMessage;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class StartView extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		// Skapa ny ReceiveHandler som k�rs i en egen tr�d och alltid ligger och 
		// lyssnar efter inkommande meddelanden
		new ReciveHandler(6789);

		Button send = (Button) findViewById(R.id.button1);
		
		// Lyssna p� knapptryckningar
		send.setOnClickListener(new OnClickListener() {

			// Definiera vad som h�nder n�r man klickar p� knappen SEND
			public void onClick(View v) {
				EditText et = (EditText) findViewById(R.id.editText1);
				//TextView tv = (TextView) findViewById(R.id.textView1);
				try {
					
					// Testmeddelande. Borche och Alice �r bara random namn �n s� l�nge
					TextMessage m = new TextMessage(MessageType.TEXT, "Borche", "Alice");
					
					// H�mta texten/meddelandet som anv�ndaren skrivit in
					m.setMessage(et.getText().toString());
					
					// Skapa en ny Sender som tar hand om att skicka meddelandet 
					// till angiven IP address p� angiven port
					new Sender(m, InetAddress.getByName("127.0.0.1"), 6789);
					//new Sender(m, InetAddress.getByName("130.236.188.128"), 6789); 
					
				} catch (IOException ie) {
					Log.d("Failade i onClick()", "Gick inte");
				}				
			}
			
		});

	}

}
