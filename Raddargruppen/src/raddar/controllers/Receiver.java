package raddar.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import raddar.models.Message;
import android.util.Log;

import com.google.gson.Gson;

public class Receiver implements Runnable {

	private Thread thread = new Thread(this);
	private BufferedReader in;
	private Socket so;
	private ReciveHandler rh;

	public Receiver(Socket so, ReciveHandler rh) {
		this.so = so;
		this.rh = rh;
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

		} catch (IOException ie) {
			ie.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

	private String extractValue(String str) {
		int index = 0;
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) == ' ') {
				index = i;
				break;
			}
		}
		return str.substring(index);
	}
}
