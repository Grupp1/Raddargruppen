package raddar.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import raddar.enums.MessageType;
import raddar.models.Message;
import raddar.models.TextMessage;
import android.util.Log;

public class Reciver implements Runnable {

	private Thread thread = new Thread(this);
	private BufferedReader in;
	private Socket so;
	private ReciveController rc;

	public Reciver(Socket so,ReciveController rc) {
		this.so = so;
		this.rc = rc;
		thread.start();
	}

	public void run() {
		try {

			in = new BufferedReader(new InputStreamReader(so.getInputStream()));
			String msgType = in.readLine();
			String[] parts = msgType.split(" ");
			String t = parts[1];
			MessageType type = MessageType.convert(t);
			Message m = null;
					
			switch (type){
			case TEXT:
				m = new TextMessage(type,in.readLine().split(" ")[1],in.readLine().split(" ")[1]);
				break;
			case IMAGE:

				break;			
			}

			Log.d( in.readLine().split(" ")[1], "hej");
			in.readLine();
			String data = "";
			while (in.ready())
				data +=in.readLine();
			m.setData(data);
			
			if(m == null || rc == null)
					Log.d("FEL",m.getData());
			rc.addToInbox(m);
			so.close();
			
		} catch (IOException ie) {
			ie.printStackTrace();
		}

	}
}
