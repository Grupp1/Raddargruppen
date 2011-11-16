package raddar.controllers;

import java.text.ParseException;
import java.util.Observable;
import java.util.Observer;

import raddar.models.ClientDatabaseManager;
import raddar.sip.IncomingCallReceiver;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.sip.SipException;
import android.net.sip.SipManager;
import android.net.sip.SipProfile;
import android.util.Log;

/*
 * Sk�ter kommunikationen med anv�ndaren och det grafiska gr�nssnittet
 */

public class SessionController implements Observer {

	private static String user;
	public static ClientDatabaseManager db;
	public static SipManager manager = null;
	public static IncomingCallReceiver callReceiver;
	public static SipProfile me;
	private static Context con;
	public static boolean hasCall = false;

	public SessionController(Context con,String user){
		this.user = user;
		this.con = con;
		
		db = new ClientDatabaseManager(con,user);
		
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.SipDemo.INCOMING_CALL");
		callReceiver = new IncomingCallReceiver();
		con.registerReceiver(callReceiver, filter);	

		me = null;
		String password = "raddar";
		String username =  "danan612";
		String domain = "ekiga.net";

		if(manager == null) {
			manager = SipManager.newInstance(con);
		}

		SipProfile.Builder builder;
		try {
			builder = new SipProfile.Builder(username, domain);

			builder.setPassword(password);
			me = builder.build();

			Intent i = new Intent();
			i.setAction("android.SipDemo.INCOMING_CALL");
			PendingIntent pi = PendingIntent.getBroadcast(con, 0, i, Intent.FILL_IN_DATA);
			manager.open(me, pi, null);

		}
		catch (ParseException e) {
			e.printStackTrace();
		} catch (SipException e) {
			e.printStackTrace();
		}

	}

	public static String getUser() {
		return user;
	}

	public void update(Observable observable, Object data) {

	}
	public static void onDestroy(){
		if (manager == null) {
			return;
		}
		try {
			if (me != null) {
				manager.close(me.getUriString());
			}
		} catch (Exception ee) {
			Log.d("WalkieTalkieActivity/onDestroy", "Failed to close local profile.", ee);
		}
		if (callReceiver != null) {
			con.unregisterReceiver(callReceiver);
		}
	}

}
