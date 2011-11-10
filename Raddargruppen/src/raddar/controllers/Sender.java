package raddar.controllers;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import com.google.gson.Gson;

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
			//Thread.sleep(10000);
			Socket so = new Socket(address, port);
			so.setSoTimeout(5000);

			Gson gson = new Gson();
			String send = message.getClass().getName()+"\r\n";
			send +=	gson.toJson(message);
			Log.d("Gson test",send);
			PrintWriter out = new PrintWriter(so.getOutputStream(), true);
			out.println(send);

			so.close();
			out.close();

		} catch (IOException ie) {
			Log.d("Skapandet av socket [2]", ie.toString());
		} //catch (InterruptedException e) {
		//Log.d("Avruten väntan", "Gick inte");
		//}
	}
}
