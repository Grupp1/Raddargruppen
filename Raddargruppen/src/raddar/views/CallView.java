package raddar.views;

import raddar.controllers.SipController;
import raddar.models.QoSManager;
import raddar.gruppen.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.net.sip.SipAudioCall;
import android.net.sip.SipException;
import android.net.sip.SipProfile;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
/**
 * The view that is shown when the user is in a call
 * @author danan612
 *
 */
public class CallView extends Activity implements OnClickListener {
	private SipAudioCall call = null;
	private Button acceptCall;
	private Button denyCall;
	private TextView callerText;
	AudioManager audioManager;
	SoundPool soundPool;
	int soundId, savedVolume;
	/**
	 * Initiate all variables to different values depending on if we are making a call
	 * or receiving a call.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.accept_call);
		Bundle extras = getIntent().getExtras();
		final String sip = (String) extras.get("sip");
		callerText = (TextView) this.findViewById(R.id.callerText);
		acceptCall = (Button) this.findViewById(R.id.acceptCall);
		acceptCall.setOnClickListener(this);
		denyCall = (Button) this.findViewById(R.id.denyCall);
		denyCall.setOnClickListener(this);

		// LJUD

		audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
		soundPool = new SoundPool(2, AudioManager.STREAM_RING, 0);
		soundId = 0;

		//audioManager.setRingerMode(2);
		// 2=normal
		// 1=vibrate
		// 0=silent

		try {
			//AssetFileDescriptor descriptor = getAssets().openFd("burp.wav");
			//soundId = soundPool.load(descriptor, 1);
			soundPool.load(this, R.raw.ringing, 1);
			Log.d( "Ljuduppspelning", "Ljud inladdat");
			soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener(){
				public void onLoadComplete(SoundPool soundPool, int soundId, int status) {
					if(status == 0){
						Log.d("onLoadComplete", "Ready");
						if (sip.equals("incoming")) {
							float volume = audioManager.getStreamVolume(AudioManager.STREAM_RING);
							float maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
							Log.d("Inkommande, volume", Float.toString(volume));
							Log.d("Inkommande, maxVolume", Float.toString(maxVolume));
							volume = volume / maxVolume;
							Log.d("Inkommande, VOLYM:", Float.toString(volume));
							soundPool.play(soundId, volume, volume, 1, -1, 1);
						}
					}
					else{
						Log.d("onLoadComplete", "Error");
						//MapUI.this.logg("Loadproblem, status=", Integer.toString(status));
					}
					//MapUI.this.logg("onLoadComplete", Integer.toString(soundId));
					//playedSound = true;
				}

			});
		}
		catch(Exception ex){
			Log.d( "Ljuduppspelning", "Fel i inladdningen av ljudfil" );
			throw new RuntimeException(ex);
		}

		// LJUD

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

				@Override
				public void onCallEnded(SipAudioCall call) {
					viewToast("Samtalet avslutades");
					finish();
				}

				@Override
				public void onError(SipAudioCall call, int errorCode,
						String errorMessage) {
					viewToast("Samtalet avbröts");
					finish();
				}
			};
			try {
				call = SipController.manager.takeAudioCall(intent, listener);
				updateText("sip:"+call.getPeerProfile().getUserName() + "@ekiga.net ringer...");
			} catch (SipException e) {
				e.printStackTrace();
			}

		} else {
			updateText("Ringer " + sip + "...");
			/*
			try {
				soundPool.load(this, R.raw.calling, 1);
				Log.d( "Ljuduppspelning", "Ljud inladdat");
				soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener(){
					@Override
					public void onLoadComplete(SoundPool soundPool, int soundId, int status) {
						if(status == 0){
							Log.d("onLoadComplete", "Ready");
//								float volume = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
//								float maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
//								Log.d("Ringer, volume", Float.toString(volume));
//								Log.d("Ringer, maxVolume", Float.toString(maxVolume));
//								volume = volume / maxVolume;
//								Log.d("Ringer, VOLYM", Float.toString(volume));
//								soundPool.play(soundId, volume, volume, 1, -1, 1);
							savedVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING);
							soundPool.setVolume(soundId, (float)0.1, (float)0.1);
							soundPool.play(soundId, (float)0.1, (float)0.1, 1, -1, (float)1);
						}
						else{
							Log.d("onLoadComplete", "Error");
						}
					}
				});
			}
			catch(Exception ex){
				Log.d( "Ljuduppspelning", "Fel i inladdningen av ljudfil" );
				throw new RuntimeException(ex);
			}
			*/
			SipController.hasCall = true;
			acceptCall.setClickable(false);
			acceptCall.setVisibility(View.INVISIBLE);
			initiateCall(sip);
		}

	}
	/**
	 * Updates the textview. Used to notify the user on the status of the call
	 * @param caller the string you want to set
	 */
	private void updateText(final String caller) {
		runOnUiThread(new Runnable() {
			public void run() {
				callerText.setText(caller);
			}
		});
	}
	/**
	 * Updates the text on the deny button
	 * @param caller The string you want to have on the denybutton
	 */
	private void updateButton(final String caller) {
		runOnUiThread(new Runnable() {
			public void run() {
				denyCall.setText(caller);
			}
		});
	}
	/**
	 * Used to initiate a call with the specified sipAddress
	 * @param sipAddress the sipaddress you want to call
	 */
	private void initiateCall(final String sipAddress) {
		try {
			SipAudioCall.Listener audioListener = new SipAudioCall.Listener() {
				@Override
				public void onCallEstablished(SipAudioCall call) {
					stopSounds();
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
					viewToast("Samtalet avslutades");
					finish();
				}
				@Override
				public void onError(SipAudioCall call, int errorCode, String errorMessage) {
					viewToast("Samtalet bröts");
					finish();
				}
				@Override
				public void onCallBusy(SipAudioCall call) {
					/*soundPool.load(CallView.this, R.raw.busy, 1);
					Log.d( "Ljuduppspelning, upptaget", "Ljud inladdat");
					soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener(){
						@Override
						public void onLoadComplete(SoundPool soundPool, int soundId, int status) {
							if(status == 0){
								Log.d("onLoadComplete", "Ready");
//									float volume = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
//									float maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
//									Log.d("Ringer, volume", Float.toString(volume));
//									Log.d("Ringer, maxVolume", Float.toString(maxVolume));
//									volume = volume / maxVolume;
//									Log.d("Ringer, VOLYM", Float.toString(volume));
//									soundPool.play(soundId, volume, volume, 1, -1, 1);
								savedVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING);
								soundPool.setVolume(soundId, (float)0.1, (float)0.1);
								soundPool.play(soundId, (float)0.1, (float)0.1, 1, 3, (float)1);
							}
							else{
								Log.d("onLoadComplete", "Error");
							}
						}
					});*/
					viewToast("Mottagaren är upptagen");
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
			updateText("Pratar med " + "sip:"+call.getPeerProfile().getUserName() + "@ekiga.net");
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
	/**
	 * Checks if the user denies or accepts an incoming call.
	 * It is also used to hang up the current call.
	 */
	public void onClick(View v) {
		stopSounds();
		if (v == acceptCall) {
			acceptCall();
			acceptCall.setClickable(false);
			acceptCall.setVisibility(View.INVISIBLE);

		} else if (v == denyCall) {
			finish();
		}
	}

	@Override
	public void onDestroy(){
		stopSounds();
		super.onDestroy();
	}

	private void viewToast(final String msg){
		runOnUiThread(new Runnable(){
			public void run(){
				Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
			}
		});
	}
	
	private void stopSounds(){
		/*
		audioManager.setStreamVolume(soundId, savedVolume, 0);
		*/
		soundPool.autoPause();
		soundPool.release();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		QoSManager.setCurrentActivity(this);
		QoSManager.setPowerMode();
	}
}
