package raddar.controllers;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Observable;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

import raddar.enums.ConnectionStatus;
import raddar.enums.MessageType;
import raddar.enums.SOSType;
import raddar.enums.ServerInfo;
import raddar.gruppen.R;
import raddar.models.ContactMessage;
import raddar.models.MapObject;
import raddar.models.MapObjectMessage;
import raddar.models.Message;
import raddar.models.NotificationMessage;
import raddar.models.OnlineUsersMessage;
import raddar.models.QoSManager;
import raddar.models.SOSMessage;
import raddar.models.You;
import raddar.views.MainView;
import raddar.views.MapUI;
import raddar.views.StartView;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.util.Log;

public class ReciveHandler extends Observable implements Runnable {
	private Thread thread = new Thread(this);

	private Context context;

	public ReciveHandler(Context context) {
		this.context = context;
		thread.start();
	}

	/**
	 * Every time new information comes in to the client a new thread is started
	 * to handle the message.
	 */
	public void run() {
		try {
			// Skapa en ServerSocket f�r att lyssna p� inkommande meddelanden
			//ServerSocket so = new ServerSocket(ServerInfo.SERVER_PORT);
			SSLServerSocketFactory sslserversocketfactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
			SSLServerSocket sslserversocket = (SSLServerSocket) sslserversocketfactory.createServerSocket(ServerInfo.SERVER_PORT);
			sslserversocket.setEnabledCipherSuites(new String[] { "SSL_DH_anon_WITH_RC4_128_MD5" });
			
			while (true) {
				// N�r ett inkommande meddelande tas emot skapa en ny Receiver
				// som k�rs i en egen tr�d
				new Receiver((SSLSocket) sslserversocket.accept(), this, context);
				notifyObservers(ConnectionStatus.CONNECTED);
			}
		} catch (IOException ie) {
			notifyObservers(ConnectionStatus.DISCONNECTED);
			Log.d("ReciveHandler",
			"Kunde inte ta emot meddelande, disconnected");
			// ie.printStackTrace();
		}

	}
	/**
	 * Method to handle incoming messages to the applicaions and send
	 * them to another class
	 * @param mt the message type of the message
	 * @param m the message
	 * @param notify true if we should notify the user
	 */
	public void newMessage(MessageType mt, final Message m, boolean notify) {
		
		if (mt == MessageType.PROBE){
			Log.d("PROBE", "POBE");
			NotificationMessage mess = new NotificationMessage(SessionController.getUser(), null);
			mess.setType(MessageType.PROBE);
			try {
				new Sender(mess);
			} catch (UnknownHostException e) {
				Log.d("Probe Sender", "Kunde inte svara servern");
			}
			// Hur ska klienten tolka att vi inte har kontakt med servern?
			// notify(ConnectionStatus.DISSCONNECT);
		}
		else if (mt == MessageType.TEXT||mt == MessageType.IMAGE) {
			DatabaseController.db.addRow(m);
		} else if (mt == MessageType.SOS) {
			//((Activity) context).runOnUiThread(new Runnable() {
			QoSManager.getCurrentActivity().runOnUiThread(new Runnable() {
				public void run() {
					if(((SOSMessage)m).getSOSType() == SOSType.ALARM){
						final You you = new You(((SOSMessage)m).getPoint(), ((SOSMessage)m).getSrcUser(), "",
								R.drawable.circle_green, ((SOSMessage)m).getStatus());
						you.setSOS(false);
						MainView.mapCont.removeObject(you, false);
						you.setSOS(true);
						MainView.mapCont.add(you, false);
						Log.d("SOS", "add: "+m.getSrcUser());
						AlertDialog.Builder alert = new AlertDialog.Builder(QoSManager.getCurrentActivity());

						alert.setTitle("SOS-meddelande fr�n: "+m.getSrcUser());
						alert.setMessage(m.getData());

						alert.setPositiveButton("G� till kartan",
								new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								// G� till kartan
								Intent intent = new Intent(QoSManager.getCurrentActivity(),MapUI.class);
								intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
								QoSManager.getCurrentActivity().startActivity(intent);
								MainView.mapCont.animateTo(you.getPoint());
							}
						});

						alert.setNegativeButton("OK",
								new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								dialog.cancel();
							}
						});

						alert.show();
					}else{
						final You you = new You(((SOSMessage)m).getPoint(), ((SOSMessage)m).getSrcUser(), "",
								R.drawable.circle_green, ((SOSMessage)m).getStatus());
						you.setSOS(false);
						MainView.mapCont.removeObject(you, false);
						you.setSOS(true);
						MainView.mapCont.add(you, false);
						Log.d("SOS", "cancel_sos: "+m.getSrcUser());
						MainView.mapCont.updateObject(you, false);
					}

				}

			});
		} else if (mt == MessageType.IMAGE) {

		}else if (mt == MessageType.MAPOBJECT) {
			MapObject mo = ((MapObjectMessage)m).toMapObject();
		
			switch(((MapObjectMessage)m).getMapOperation()){
		
			case ADD:
				MainView.mapCont.add(mo,false);
				break;
			case REMOVE:
				MainView.mapCont.removeObject(mo, false);
				break;
			case UPDATE:
				MainView.mapCont.updateObject(mo,false);
				break;
			default:

			}
		}
		else if(mt == MessageType.CONTACT){
			if(((ContactMessage)m).getContact().equals(SessionController.getUser())){
				//TAGIT BORT RETURN FÖR LÄTTARE TEST
				//return;
			}
			DatabaseController.db.addRow(((ContactMessage)m).toContact());
		}
		else if(mt == MessageType.ONLINE_USERS){
			if(!((OnlineUsersMessage)m).getUserName().equals(SessionController.getUser())){
				switch(((OnlineUsersMessage) m).getOnlineOperation()){
				case ADD:
					SessionController.getSessionController().addOnlineUser(((OnlineUsersMessage)m).getUserName());
					Log.d("ONLINE_USER TRUE", ((OnlineUsersMessage)m).getUserName());
					break;
				case REMOVE:
					SessionController.getSessionController().removeOnlineUser(((OnlineUsersMessage)m).getUserName());
					Log.d("ONLINE_USER FALSE", ((OnlineUsersMessage)m).getUserName());
					break;
				default:
					break;
				}
			}
		}
		else if(mt == MessageType.NOTIFICATION){
			Log.e("LOGOUT","OTHER USER HAS LOGGED IN ON ANOTHER DEVICE");
			
			QoSManager.getCurrentActivity().runOnUiThread(new Runnable() {
				public void run() {
					AlertDialog.Builder alert = new AlertDialog.Builder(QoSManager.getCurrentActivity());

					alert.setTitle("Forcerad utloggning");
					alert.setMessage(m.getData());
					alert.setOnCancelListener(new OnCancelListener(){
						public void onCancel(DialogInterface dialog) {
							Intent intent = new Intent(QoSManager.getCurrentActivity(),StartView.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							QoSManager.getCurrentActivity().startActivity(intent);
						}
					});
					alert.show();
				}
			});
		}

	}
}
