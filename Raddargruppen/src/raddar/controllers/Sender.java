package raddar.controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import raddar.enums.ConnectionStatus;
import raddar.enums.ServerInfo;
import raddar.models.LoginManager;
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
	private String send;

	private Sender(InetAddress address, int port){
		this.address = address;
		this.port = port;
	}


	public Sender(Message message, InetAddress address, int port) {
		this(address, port);
		this.message = message;
		thread.start();
	}
	public Sender(Message message) throws UnknownHostException {
		this.message = message;
		this.port = ServerInfo.SERVER_PORT;
		this.address = InetAddress.getByName(ServerInfo.SERVER_IP);
		thread.start();
	}
	
	public Sender(String send) throws UnknownHostException{
		this.send = send;
		this.port = ServerInfo.SERVER_PORT;
		this.address = InetAddress.getByName(ServerInfo.SERVER_IP);
		thread.start();
	}

	public void run() {
		Gson gson = new Gson();
		if(message!=null){
			send = message.getClass().getName()+"\r\n";
			send +=	gson.toJson(message);	
		}
		Log.d("Sending", "Sending: "+send);
		try {
			
			/* Gamla anslutningen
			Socket so = new Socket(address, port);
			so.setSoTimeout(5000);
			PrintWriter out = new PrintWriter(so.getOutputStream(), true);
			out.println(send);
			so.close();
			out.close();

			*/
			
			//nya ssl varianten
			SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
			SSLSocket sslsocket = (SSLSocket) sslsocketfactory.createSocket(address, port);
			PrintWriter out = new PrintWriter(sslsocket.getOutputStream(), true);
			out.println(send);
			sslsocket.close();
			out.close();
			
			
			Log.e("Sendzordz",send);

		} catch (IOException ie) {
			Log.d("Skapandet av socket [2]", ie.toString());
			SessionController.getSessionController().changeConnectionStatus(ConnectionStatus.DISCONNECTED);
			LoginManager lm = new LoginManager();
			lm.evaluate(SessionController.getUserName(), SessionController.getPassword(),false);
			
			DatabaseController.db.addBufferedMessageRow(send);
		} 
//		catch (SocketException e) {
//			
//		}
	}

}

