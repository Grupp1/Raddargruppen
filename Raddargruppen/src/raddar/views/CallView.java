package raddar.views;

import raddar.controllers.SessionController;
import raddar.gruppen.R;
import android.app.Activity;
import android.content.Intent;
import android.net.sip.SipAudioCall;
import android.net.sip.SipException;
import android.net.sip.SipManager;
import android.net.sip.SipProfile;
import android.net.sip.SipSession;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class CallView extends Activity implements OnClickListener{
	private SipAudioCall incomingCall = null;
	private Button acceptCall;
	private Button denyCall;
	private TextView callerText;
	private SipSession sipSession;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.walkietalkie);
		Bundle extras = getIntent().getExtras();
		Intent intent = (Intent) extras.get("intent");

		acceptCall = (Button)this.findViewById(R.id.acceptCall);
		acceptCall.setOnClickListener(this);

		denyCall = (Button)this.findViewById(R.id.denyCall);
		denyCall.setOnClickListener(this);

		callerText = (TextView)this.findViewById(R.id.callerText);

		SipAudioCall.Listener listener = new SipAudioCall.Listener() {
			@Override
			public void onRinging(SipAudioCall call, SipProfile caller) {
				try {
					call.answerCall(30);
				} catch (Exception e) {
					Log.d("Andreas ringer",e.toString());
				}
			}
			public void onCallEnded(SipAudioCall call) {
				denyCall();
			}
		};
		try {	
			sipSession = SessionController.manager.getSessionFor(intent);
			incomingCall = SessionController.manager.takeAudioCall(intent, listener);
			callerText.setText(incomingCall.getPeerProfile().getUserName()+" ringer...");
		} catch (SipException e) {
			e.printStackTrace();
		}	
	}
	public void onPause(){
		super.onPause();
		SessionController.hasCall = false;
	}

	private void denyCall(){
		try {		
			if (incomingCall != null) {
				incomingCall.close();
				incomingCall.endCall();
				sipSession.endCall();
			}
		} catch (SipException e) {
			e.printStackTrace();
		}	
		finish();
	}

	private void acceptCall(){
		try{
			callerText.setText("Pratar med "+incomingCall.getPeerProfile().getUserName());
			incomingCall.answerCall(30);
			incomingCall.startAudio();
			incomingCall.setSpeakerMode(true);
			if(incomingCall.isMuted()) {
				incomingCall.toggleMute();
			}
		} catch (Exception e) {
			if (incomingCall != null) {
				incomingCall.close();
			}

		}
	}

	public void onClick(View v) { 
		if(v == acceptCall){
			acceptCall();
			acceptCall.setClickable(false);
			acceptCall.setVisibility(View.INVISIBLE);
			
		}
		else if(v == denyCall){
			denyCall();
		}
	}
}
