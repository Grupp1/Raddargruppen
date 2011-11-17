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

public class CallView extends Activity implements OnClickListener {
	private SipAudioCall call = null;
	private Button acceptCall;
	private Button denyCall;
	private TextView callerText;
	private SipSession sipSession;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.walkietalkie);
		Bundle extras = getIntent().getExtras();
		String sip = (String) extras.get("sip");
		callerText = (TextView) this.findViewById(R.id.callerText);
		acceptCall = (Button) this.findViewById(R.id.acceptCall);
		acceptCall.setOnClickListener(this);
		denyCall = (Button) this.findViewById(R.id.denyCall);
		denyCall.setOnClickListener(this);
		if (sip.equals("incoming")) {
			Intent intent = (Intent) extras.get("intent");
			SipAudioCall.Listener listener = new SipAudioCall.Listener() {
				@Override
				public void onRinging(SipAudioCall call, SipProfile caller) {
					try {
						call.answerCall(30);
					} catch (Exception e) {
						Log.d("Andreas ringer", e.toString());
					}
				}

				public void onCallEnded(SipAudioCall call) {
					denyCall();
				}
			};
			try {
				sipSession = SessionController.manager.getSessionFor(intent);
				call = SessionController.manager
						.takeAudioCall(intent, listener);
				updateText(call.getPeerProfile().getUserName() + " ringer...");
			} catch (SipException e) {
				e.printStackTrace();
			}

		} else {
			updateText("Ringer " + sip + "...");
			SessionController.hasCall = true;
			acceptCall.setClickable(false);
			acceptCall.setVisibility(View.INVISIBLE);
			initiateCall(sip);
		}

	}

	private void updateText(final String caller) {
		runOnUiThread(new Runnable() {
			public void run() {
				callerText.setText(caller);
			}
		});
	}
	private void updateButton(final String caller) {
		runOnUiThread(new Runnable() {
			public void run() {
				denyCall.setText(caller);
			}
		});
	}

	private void initiateCall(final String sipAddress) {
		Log.d("EINAR", sipAddress);
		try {
			SipAudioCall.Listener audioListener = new SipAudioCall.Listener() {
				@Override
				public void onCallEstablished(SipAudioCall call) {
					updateText("Pratar med " + sipAddress);
					updateButton("Lägg på");
					call.startAudio();
					call.setSpeakerMode(true);
					if (call.isMuted()) {
						call.toggleMute();
					}
				}

				@Override
				public void onCallEnded(SipAudioCall session) {
					finish();
				}
				@Override
				public void onError(SipAudioCall call, int errorCode,
						String errorMessage) {
						finish();
				}
			};
			SipSession.Listener sessionListener = new SipSession.Listener() {
				@Override
				public void onCallBusy(SipSession session) {
					finish();
				}

				@Override
				public void onCallEnded(SipSession session) {
					finish();
				}
			};
			call = SessionController.manager.makeAudioCall(
					SessionController.me.getUriString(), sipAddress,
					audioListener, 30);

		} catch (Exception e) {
			Log.i("WalkieTalkieActivity/InitiateCall",
					"Error when trying to close manager.", e);
			if (SessionController.me != null) {
				try {
					SessionController.manager.close(SessionController.me
							.getUriString());
				} catch (Exception ee) {
					Log.i("WalkieTalkieActivity/InitiateCall",
							"Error when trying to close manager.", ee);
					ee.printStackTrace();
				}
			}
			if (call != null) {
				call.close();
			}
		}
	}

	public void onPause() {
		super.onPause();
		try {
			if (call != null) {
				call.close();
				call.endCall();
			}
			if (sipSession != null) {
				sipSession.endCall();
			}
		} catch (SipException e) {
			e.printStackTrace();
		}
		SessionController.hasCall = false;
	}

	private void denyCall() {
		finish();
	}

	private void acceptCall() {
		try {
			updateText("Pratar med " + call.getPeerProfile().getUserName());
			updateButton("Lägg på");
			call.answerCall(30);
			call.startAudio();
			call.setSpeakerMode(true);
			if (call.isMuted()) {
				call.toggleMute();
			}
		} catch (Exception e) {
			if (call != null) {
				call.close();
			}

		}
	}

	public void onClick(View v) {
		if (v == acceptCall) {
			acceptCall();
			acceptCall.setClickable(false);
			acceptCall.setVisibility(View.INVISIBLE);

		} else if (v == denyCall) {
			denyCall();
		}
	}
}
