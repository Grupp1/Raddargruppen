package raddar.controllers;

import java.text.ParseException;

import raddar.sip.IncomingCallReceiver;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.sip.SipException;
import android.net.sip.SipManager;
import android.net.sip.SipProfile;
import android.util.Log;
/**
 * Provides static references to importent information used to make and
 * recieve calls.
 * @author danan612
 *
 */
public class SipController {
	public static SipManager manager = null;
	public static IncomingCallReceiver callReceiver;
	public static SipProfile me = null;
	public static boolean hasCall = false;
	private static Context con;
	private static String[] sipDetails = null; 
	
	/**
	 * Creates a new sipcontroller and logs in to the sip server so that we are able to receive
	 * calls.
	 * @param con The Context that will receive incoming calls.
	 */
	public SipController(Context con){
		this.con = con; 
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.SipDemo.INCOMING_CALL");
		callReceiver = new IncomingCallReceiver();
		con.registerReceiver(callReceiver, filter);	

		if(sipDetails == null)
			return;
		if(manager == null) {
			manager = SipManager.newInstance(con);
		}

		SipProfile.Builder builder;
		try {
			builder = new SipProfile.Builder(sipDetails[0], sipDetails[2]);

			builder.setPassword(sipDetails[1]);
			me = builder.build();
			Log.d("SesionController",sipDetails[0]+" "+sipDetails[1]+" "+" "+sipDetails[2]);
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
	/**
	 * Closes our connection to the sip server.
	 * Must be run before closing the application.
	 */
	public static void onClose(){
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
	/**
	 * Sets our sip login details
	 * @param sip The sipDetails to be set. Must be a string array where the 
	 * first cell is the user name, the second cell is the password and the third cell the
	 * sip server address.
	 */
	public static void setSipDetails(String[] sip){
		if(sip.length != 3) return;
		sipDetails = sip;
	}
}
