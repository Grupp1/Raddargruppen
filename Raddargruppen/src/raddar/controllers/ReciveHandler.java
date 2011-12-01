package raddar.controllers;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Observable;

import raddar.enums.ConnectionStatus;
import raddar.enums.MessageType;
import raddar.enums.ServerInfo;
import raddar.models.ContactMessage;
import raddar.models.ImageMessage;
import raddar.models.MapObject;
import raddar.models.MapObjectMessage;
import raddar.models.Message;
import raddar.models.SOSMessage;
import raddar.views.MainView;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
			// Skapa en ServerSocket för att lyssna på inkommande meddelanden
			ServerSocket so = new ServerSocket(ServerInfo.SERVER_PORT);

			while (true) {
				// När ett inkommande meddelande tas emot skapa en ny Receiver
				// som körs i en egen tråd
				new Receiver(so.accept(), this, context);
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
		if (mt == MessageType.TEXT) {
			DatabaseController.db.addRow(m, notify);
		} else if (mt == MessageType.SOS) {
			
			((Activity) context).runOnUiThread(new Runnable() {
				public void run() {
					AlertDialog.Builder alert = new AlertDialog.Builder(context);

					alert.setTitle("SOS");
					alert.setMessage(m.getData());

					alert.setPositiveButton("Gå till kartan",
							new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							
							// Gå till kartan
						}
					});

					alert.setNegativeButton("Gör inget",
							new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							dialog.cancel();
						}
					});

					alert.show();
				}

			});
		} else if (mt == MessageType.IMAGE) {
			ImageMessage im = (ImageMessage) m;
			DatabaseController.db.addImageMessageRow(im);


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
			DatabaseController.db.addRow(((ContactMessage)m).toContact());
		}
		else if(mt == MessageType.NOTIFICATION){
//			((Activity) context).runOnUiThread(new Runnable() {
//				public void run() {
//					AlertDialog.Builder alert = new AlertDialog.Builder(context);
//
//					alert.setTitle("Forcerad utloggning");
//					alert.setMessage("En annan klient har loggat in på denna användare. Du kommer nu att loggas ut.");
//					//					alert.setNegativeButton("Gör inget",
//					//							new DialogInterface.OnClickListener() {
//					//						public void onClick(DialogInterface dialog,
//					//								int whichButton) {
//					//							dialog.cancel();
//					//						}
//					//					});
//					alert.setOnCancelListener(new OnCancelListener(){
//						public void onCancel(DialogInterface dialog) {
//							setChanged();
//							notifyObservers("LOGOUT");
//						}
//					});
//
//					alert.show();
//				}
//
//			});
		}

	}
}
