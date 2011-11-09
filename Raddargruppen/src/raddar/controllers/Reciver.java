package raddar.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.text.DateFormat;

import com.google.gson.Gson;

import raddar.enums.MessageType;
import raddar.models.Message;
import raddar.models.TextMessage;
import android.util.Log;

public class Reciver implements Runnable {

	private Thread thread = new Thread(this);
	private BufferedReader in;
	private Socket so;
	private ReciveHandler rh;

	public Reciver(Socket so,ReciveHandler rh) {
		this.so = so;
		this.rh = rh;
		thread.start();
	}

	public void run() {
		try {

			in = new BufferedReader(new InputStreamReader(so.getInputStream()));
			/*
			String msgType = in.readLine();
			String[] parts = msgType.split(" ");
			String t = parts[1];
			MessageType type = MessageType.convert(t);
			Message m = null;
			String prio = in.readLine().split(" ")[1];
			String srcUser = in.readLine().split(" ")[1];
			String toUser = in.readLine().split(" ")[1];
			in.readLine();
			//	String[] dateParts = in.readLine().split(" ");
			//	DateFormat df = DateFormat.getDateTimeInstance();
			//	Date  date = new Date(Date.parse(in.readLine()));
			String subject = in.readLine();
			subject = extractValue(subject);
			switch (type){
			case TEXT:
				m = new TextMessage(type,srcUser,toUser);
				//	m.setDate(date);
				m.setSubject(subject);
				in.readLine();
				String data = "";
				while (in.ready())
					data +=in.readLine();
				m.setData(data);
				break;
			case IMAGE:

				break;			
			}

			*/
			//Läser av typen
		//	Class c = Class.forName(in.readLine());
			String temp = in.readLine();
			MessageType type = MessageType.convert(temp);
			switch (type){
			case TEXT:
				temp =in.readLine();
				Log.d("Reciver","temp");
				Gson gson = new Gson();
				Message m = gson.fromJson(temp,TextMessage.class);
				so.close();
				rh.newMessage(m.getType(),m);
				break;
			case IMAGE:

				break;			
			}
			

		} catch (IOException ie) {
			ie.printStackTrace();
		}
		//FIXA SEDAN, ENDAST FÖR TEST ArrayIndexOutOfBounds borde stå här
		catch(Exception e){
			Log.d("Undersök",e+"");
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
