package raddar.controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import raddar.enums.ServerInfo;
import raddar.models.MapObject;
import raddar.models.Message;
import android.util.Log;

import com.google.gson.Gson;

public class Sender implements Runnable {

	private Thread thread = new Thread(this);
	// Servers address
	private InetAddress address;
	// Serverns port
	private int port;
	// Meddelandet som ska skickas
	private Message message;
	// MapObject som ska skickas
	private MapObject mapObject;

	private Sender(InetAddress address, int port){
		this.address = address;
		this.port = port;
	}

	
	public Sender(Message message, InetAddress address, int port) {
		this(address, port);
		this.message = message;
		thread.start();
	}

	public Sender(MapObject mapObject, InetAddress address, int port) {
		this(address, port);
		this.mapObject = mapObject;
		thread.start();
	}
	
	public Sender(Message message) throws UnknownHostException {
		this.message = message;
		this.port = ServerInfo.SERVER_PORT;
		this.address = InetAddress.getByName(ServerInfo.SERVER_IP);
		thread.start();
	}

	public void run() {
		try {
			Socket so = new Socket(address, port);
			so.setSoTimeout(5000);
			Gson gson = new Gson();
			String send = null;
			if(message!=null){
				send = message.getClass().getName()+"\r\n";
				send +=	gson.toJson(message);	
			}else if(mapObject!=null){
				send = mapObject.getClass().getName()+"\r\n";
				send +=	gson.toJson(mapObject);
			}
			Log.d("Gson test",send);
			PrintWriter out = new PrintWriter(so.getOutputStream(), true);

			out.println(send);

			so.close();
			out.close();


		} catch (IOException ie) {
			Log.d("Skapandet av socket [2]", ie.toString());
		} //catch (InterruptedException e) {
		//Log.d("Avbruten v�ntan", "Gick inte");
		//}

	}
}
