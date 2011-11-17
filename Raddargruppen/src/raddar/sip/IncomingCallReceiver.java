package raddar.sip;

import raddar.controllers.SessionController;
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
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("Einar ringer", "Nu ringer vi");
		if (SessionController.hasCall){
			Log.d("Einar ringer", "Deny call");
			try {
				SessionController.manager.getSessionFor(intent).endCall();
			} catch (SipException e) {
				e.printStackTrace();
			}
			return;
		}
		SessionController.hasCall = true;
		Intent nextIntent = new Intent(context, CallView.class);
		nextIntent.putExtra("intent", intent);
		nextIntent.putExtra("sip", "incoming");
		context.startActivity(nextIntent);
	}
}
