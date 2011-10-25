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
		new ReciveHandler(6789);

		Button send = (Button) findViewById(R.id.button1);		
		send.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				//EditText et = (EditText) findViewById(R.id.editText1);
				//TextView tv = (TextView) findViewById(R.id.textView1);
				try {
					
					TextMessage m = new TextMessage(MessageType.TEXT, "Borche", "Alice");
					m.setMessage("Hej");
					new Sender(m, 
							InetAddress.getByName("130.236.188.128"), 6789); 
					
					
					/*Socket so = new Socket(InetAddress.getByName("130.236.188.128"), 6789);
					so.setSoTimeout(5000);
					
					TextMessage tm = new TextMessage(MessageType.TEXT, "Borche", "Alice");
					tm.setMessage(et.getText().toString());
									
					//et.setText(tm.getFormattedMessage());
					
					PrintWriter out = new PrintWriter(so.getOutputStream(), true);
					BufferedReader in = new BufferedReader(new InputStreamReader(so.getInputStream()));
					
					// Formatera och skicka meddelandet till servern
					out.println(tm.getFormattedMessage());
					
					String msgType = in.readLine().split(" ")[1];
					String msgPriority = in.readLine().split(" ")[1];
					String fromUser = in.readLine().split(" ")[1];
					String toUser = in.readLine().split(" ")[1];
					in.readLine();
					String data = "";
					while (in.ready())
						data += in.readLine();
					
					
					MessageType type = MessageType.convert(msgType);
					MessagePriority priority = MessagePriority.convert(msgPriority);
					
					TextMessage reply = new TextMessage(type, fromUser, toUser, priority, data);
					
					tv.setText(reply.getFormattedMessage());		
					
					so.close();
					*/
					
				} catch (IOException ie) {
					Log.d("Failade i onClick()", "Gick inte");
				}				
			}
			
		});

	}

}
