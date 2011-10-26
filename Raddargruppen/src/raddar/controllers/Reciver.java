package raddar.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import raddar.enums.MessageType;
import android.util.Log;

public class Reciver implements Runnable {

	private Thread thread = new Thread(this);
	private BufferedReader in;
	private Socket so;

	public Reciver(Socket so) {
		this.so = so;
		thread.start();
	}

	public void run() {
		try {
			
			in = new BufferedReader(new InputStreamReader(so.getInputStream()));
			String msgType = in.readLine();
			String[] parts = msgType.split(" ");
			String t = parts[1];

			MessageType type = MessageType.convert(t);
			Log.d( in.readLine().split(" ")[1], "hej");
			Log.d( in.readLine().split(" ")[1], "hej");
			Log.d( in.readLine().split(" ")[1], "hej");
			in.readLine();
			while (in.ready())
				Log.d(in.readLine(),"hej");
			
			/*switch (type){
			case TEXT:

				break;
			case IMAGE:

				break;			
			}*/

		} catch (IOException ie) {
			ie.printStackTrace();
		}

	}
}
