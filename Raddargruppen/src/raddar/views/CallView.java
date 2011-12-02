package raddar.views;

import raddar.controllers.SessionController;
import raddar.controllers.SipController;
import raddar.gruppen.R;
import raddar.models.QoSManager;
import android.app.Activity;
import android.content.Intent;
import android.net.sip.SipAudioCall;
import android.net.sip.SipException;
import android.net.sip.SipProfile;
import android.net.sip.SipSession;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
/**
 * The view that is shown when the user is in a call
 * @author danan612
 *
 */
public class CallView extends Activity implements OnClickListener {
	private SipAudioCall call = null;
	private ImageButton acceptCall;
	private ImageButton denyCall;
	private TextView callerText;
	private TextView infoText;
	/**
	 * Initiate all variables to different values depending on if we are making a call
	 * or receiving a call.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.walkietalkie);
		SessionController.titleBar(this, " - Samtal");
		Bundle extras = getIntent().getExtras();
		String sip = (String) extras.get("sip");
		String dstUser = (String) extras.get("dstUser");
		infoText = (TextView) this.findViewById(R.id.infotext1);
		callerText = (TextView) this.findViewById(R.id.callerText);
		acceptCall = (ImageButton) this.findViewById(R.id.acceptCall);
		acceptCall.setOnClickListener(this);
		denyCall = (ImageButton) this.findViewById(R.id.denyCall);
		denyCall.setOnClickListener(this);
		if (sip.equals("incoming")) {
			Intent intent = (Intent) extras.get("intent");
			SipAudioCall.Listener listener = new SipAudioCall.Listener() {
				@Override
				public void onRinging(SipAudioCall call, SipProfile caller) {
					try {
						call.answerCall(30);
					} catch (Exception e) {
						
					}
				}
				

				public void onCallEnded(SipAudioCall call) {
					finish();
				}

				@Override
				public void onError(SipAudioCall call, int errorCode,
						String errorMessage) {
					finish();
				}
			};
			try {
				call = SipController.manager
						.takeAudioCall(intent, listener);
				updateText("Samtal från",  call.getPeerProfile().getUserName());
			} catch (SipException e) {
				e.printStackTrace();
			}

		} else {
			updateText("Ringer ", dstUser);
			SipController.hasCall = true;
			acceptCall.setClickable(false);
			acceptCall.setVisibility(View.INVISIBLE);
			initiateCall(sip, dstUser);
		}

	}
	/**
	 * Updates the textview. Used to notify the user on the status of the call
	 * @param caller the string you want to set
	 */
	private void updateText(final String info, final String caller) {
		runOnUiThread(new Runnable() {
			public void run() {
				Log.d("info", info);
				Log.d("caller", caller);
				
																		
				infoText.setText(info);
				callerText.setText(caller);
			}
		});
	}
	
	
	/**
	 * Used to initiate a call with the specified sipAddress
	 * @param sipAddress the sipaddress you want to call
	 */
	private void initiateCall(final String sipAddress, final String dst) {
		try {
			SipAudioCall.Listener audioListener = new SipAudioCall.Listener() {
				@Override
				public void onCallEstablished(SipAudioCall call) {
					updateText("Pratar med ", dst);
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
				@Override
				public void onCallBusy(SipAudioCall call) {
					finish();
				}
			};
			
			call = SipController.manager.makeAudioCall(
					SipController.me.getUriString(), sipAddress,
					audioListener, 30);

		} catch (Exception e) {
			Log.i("WalkieTalkieActivity/InitiateCall",
					"Error when trying to close manager.", e);
			if (SipController.me != null) {
				try {
					SipController.manager.close(SipController.me
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
	/**
	 * Called when the activity is beeing paused.
	 * Shuts down all current calls
	 */
	public void onPause() {
		super.onPause();
		try {
			if (call != null) {
				call.endCall();
				call.close();
			}else Log.d("call", "är null");
		} catch (SipException e) {
			e.printStackTrace();
		}
		SipController.hasCall = false;
	}
	/**
	 * Accepts the incoming call and starts it
	 */
	private void acceptCall() {
		try {
			updateText("Pratar med ", call.getPeerProfile().getUserName());
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
	/**
	 * Checks if the user denies or accepts an incoming call.
	 * It is also used to hang up the current call.
	 */
	public void onClick(View v) {
		if (v == acceptCall) {
			acceptCall();
			acceptCall.setClickable(false);
			acceptCall.setVisibility(View.INVISIBLE);

		} else if (v == denyCall) {
			finish();
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		QoSManager.setCurrentActivity(this);
		QoSManager.setPowerMode();
	}
}
