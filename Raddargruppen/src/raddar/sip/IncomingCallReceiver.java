package raddar.sip;

import raddar.controllers.SessionController;
import raddar.controllers.SipController;
import raddar.views.CallView;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.sip.SipAudioCall;
import android.net.sip.SipException;
import android.net.sip.SipProfile;
import android.util.Log;

public class IncomingCallReceiver extends BroadcastReceiver {
	/**
	 * Called when we have an incoming call.
	 * If we are not busy in a current call, start the CallView
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		if (SipController.hasCall){
			try {
				SipController.manager.getSessionFor(intent).endCall();
			} catch (SipException e) {
				e.printStackTrace();
			}
			return;
		}
		SipController.hasCall = true;
		Intent nextIntent = new Intent(context, CallView.class);
		nextIntent.putExtra("intent", intent);
		nextIntent.putExtra("sip", "incoming");
		context.startActivity(nextIntent);
	}
}
