package raddar.controllers;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import raddar.enums.MessagePriority;
import raddar.enums.MessageType;
import raddar.models.Message;
import raddar.models.TextMessage;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

public class Sender implements Runnable {
	
	private Thread thread = new Thread(this);
	// Servers address
	private InetAddress address;
	// Serverns port
	private int port;
	// Meddelandet som ska skickas
	private Message message;
		
	public Sender(Message message, InetAddress address, int port) {
		this.message = message;
		this.port = port;
		this.address = address;
		thread.start();
	}

	public void run() {
		try {
			Log.d("1", "lawl");
			Socket so = new Socket(address, port);
			Log.d("2", "lawl");
			so.setSoTimeout(5000);
			Log.d("3", "lawl");
									
			PrintWriter out = new PrintWriter(so.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(so.getInputStream()));
			
			// Formatera och skicka meddelandet till servern
			out.println(message.getFormattedMessage());
						
			/*String msgType = in.readLine().split(" ")[1];
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
			
			tv.setText(reply.getFormattedMessage()); */		
			
			so.close();
			
		} catch (IOException ie) {
			Log.d("Skapandet av socket [2]", "Gick inte");
		} 
	}

}
