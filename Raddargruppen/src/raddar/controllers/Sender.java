package raddar.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import raddar.enums.MessagePriority;
import raddar.enums.MessageType;
import raddar.gruppen.R;
import raddar.models.TextMessage;
import android.app.Activity;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

public class Sender extends Activity implements Runnable {
	
	private Thread thread;
	// Serverns port
	private int port = 6789;
	private TextView tv;
	private EditText et;
	
	public Sender() {
		tv = (TextView) findViewById(R.id.textView1);
		et = (EditText) findViewById(R.id.editText1);
		thread.start();
	}

	public void run() {
		try {
			Socket so = new Socket(InetAddress.getLocalHost(), port);
			so.setSoTimeout(5000);
			
			TextMessage tm = new TextMessage(MessageType.TEXT, "Borche", "Alice");
			tm.setMessage(et.getText().toString());
			
			PrintWriter out = new PrintWriter(so.getOutputStream());
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
			
			
		} catch (IOException ie) {
			Log.d("Skapandet av socket", "Gick inte");
		}
	}

}
