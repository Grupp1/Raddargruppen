package raddar.controllers;

import raddar.views.ReadMessageView;
import android.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class NotificationService extends Service implements Runnable {
	
	private Thread thrd = new Thread(this);
	
	private CharSequence contentText = "Default text";
	private String message[];
	
	@Override
	public void onCreate() {
		thrd.start();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		
		
		Bundle b = intent.getExtras();
		if (b == null) Log.d("TEST BB", (String) contentText);
		
		message = b.getStringArray("msg");
		contentText = message[1];
		return 1;
	}

	public void run() {
		NotificationManager nManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
		
		int icon = R.drawable.sym_action_email;
		CharSequence tickerText = "Meddelande";
		long when = System.currentTimeMillis();
		
		Notification notification = new Notification(icon, tickerText, when);
		
		Context context = getApplicationContext();
		CharSequence contentTitle = "Nytt meddelande";
		Intent notificationIntent = new Intent(this, ReadMessageView.class);
		notificationIntent.putExtra("sender",message[0]);
		notificationIntent.putExtra("subject",message[1]);
		notificationIntent.putExtra("data",message[2]);
		notificationIntent.putExtra("date", message[3]);
		notificationIntent.putExtra("type", message[4]);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		
		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		
		long[] vibrate = {0, 100, 200, 300, 200, 500, 400, 2000};
		
		notification.defaults |= Notification.DEFAULT_SOUND;
		notification.vibrate = vibrate;
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		
		nManager.notify(1, notification);
		
		stopSelf();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
