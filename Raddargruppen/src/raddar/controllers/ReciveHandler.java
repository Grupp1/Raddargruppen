package raddar.controllers;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Observable;

import raddar.enums.ConnectionStatus;
import raddar.enums.MessageType;
import raddar.enums.ServerInfo;
import raddar.models.ImageMessage;
import raddar.models.MapObject;
import raddar.models.Message;
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

		}
	}

	public void newMapObject(MapObject o) {
		if (MainView.mapCont.getThread().isAlive()) {
			MainView.mapCont.add(o);
		} else {
			DatabaseController.db.addRow(o);
		}

	}
}
