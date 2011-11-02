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
			
			// Formatera och skicka meddelandet till servern
			out.println(message.getFormattedMessage());
			
			so.close();
			
		} catch (IOException ie) {
			Log.d("Skapandet av socket [2]", "Gick inte");
		} 
	}

}
