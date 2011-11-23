package raddar.controllers;

import java.io.IOException;
import java.net.ServerSocket;

import raddar.enums.MessageType;
import raddar.models.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;

public class ReciveHandler implements Runnable {
	// Standard port = 6789
	private int port = 4043;
	private Thread thread = new Thread(this);

	private Context context;

	public ReciveHandler(Context context) {
		this(4043, context);
	}

	public ReciveHandler(int port, Context context) {
		this.port = port;
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
			ServerSocket so = new ServerSocket(port);

			while (true)
				// N�r ett inkommande meddelande tas emot skapa en ny Receiver
				// som k�rs i en egen tr�d
				new Receiver(so.accept(), this, context);

		} catch (IOException ie) {
			ie.printStackTrace();
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

					alert.setPositiveButton("G� till kartan",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									// G� till kartan
								}
							});

					alert.setNegativeButton("G�r inget",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									dialog.cancel();
								}
							});

					alert.show();
				}

			});
		}
	}
}
