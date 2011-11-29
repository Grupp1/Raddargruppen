package raddar.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import raddar.enums.MessageType;
import raddar.models.MapObject;
import raddar.models.Message;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;

public class Receiver implements Runnable {

	private Thread thread = new Thread(this);
	private BufferedReader in;
	private Socket so;
	private ReciveHandler rh;

	private Context context;

	public Receiver(Socket so, ReciveHandler rh, Context context) {
		this.so = so;
		this.rh = rh;
		this.context = context;
		thread.start();
	}

	public void run() {
		try {
			in = new BufferedReader(new InputStreamReader(so.getInputStream()));

			String test = in.readLine();
			boolean notify = false;
			Message m = null;
			while (test != null) {
				Class c = Class.forName(test);
				String temp = in.readLine();
				Object o = new Gson().fromJson(temp, c);
				Log.d("RECEIVE",o.toString());
				// if message
				if (o instanceof Message){
					m = (Message) o;
					if(m.getType() == MessageType.REQUEST)
						notify = true;
					rh.newMessage(m.getType(), m,notify);
					Log.d("test", test + " ");
				}
				// if mapobject
				else if (o instanceof MapObject){
					MapObject mo = (MapObject) o;
					rh.newMapObject(mo);
				}
				test = in.readLine();
			}
			so.close();

			Intent intent = new Intent(context, NotificationService.class);
			if (m != null&& !notify)
				context.startService(intent.putExtra("msg", m.getSubject()));


		} catch (IOException ie) {
			Log.d("Receiver", "IOException");
			//ie.printStackTrace();

		} catch (ArrayIndexOutOfBoundsException e) {
			Log.d("Unders�k", "ArrayIndexOutOfBounds i receiver");
		} catch (ClassNotFoundException e) {
			Log.e("ClassnotFoundException", e.toString());
			return;
		}

	}
}
