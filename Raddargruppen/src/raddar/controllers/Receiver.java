package raddar.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import raddar.enums.MessageType;
import raddar.enums.SOSType;
import raddar.models.MapObject;
import raddar.models.Message;
import raddar.models.SOSMessage;
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
			boolean notify = true;
			Message m = null;
			Gson gson = new Gson();
			while (test != null) {
				Class c = Class.forName(test);
				String temp = in.readLine();
				m = gson.fromJson(temp, c);
				if(!(m.getType() == MessageType.TEXT))
					notify = false;
				rh.newMessage(m.getType(), m,notify);

				test = in.readLine();
			}
			so.close();

			if (m != null && notify) {
				Intent intent = new Intent(context, NotificationService.class);
				context.startService(intent.putExtra("msg", m.getSubject()));
			}
		} catch (IOException ie) {
			ie.printStackTrace();
		} catch (ArrayIndexOutOfBoundsException e) {
			Log.d("Unders�k", "ArrayIndexOutOfBounds i receiver");
		} catch (ClassNotFoundException e) {
			Log.e("ClassnotFoundException", e.toString());
			return;
		}

	}
}
