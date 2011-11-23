package raddar.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import raddar.models.Message;
import raddar.views.InboxView;
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

			Class c = Class.forName(in.readLine());
			String temp = in.readLine();
			Log.d("Reciver", "temp");
			Message m = new Gson().fromJson(temp, c);
			

			so.close();
			rh.newMessage(m.getType(), m);
			
			Intent intent = new Intent(context, NotificationService.class);
			context.startService(intent.putExtra("msg", m.getSubject()));
			
			
		} catch (IOException ie) {
			ie.printStackTrace();

		}
		catch(ArrayIndexOutOfBoundsException e){
			Log.d("Undersök","ArrayIndexOutOfBounds i receiver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}
}
