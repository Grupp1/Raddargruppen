package raddar.views;

import raddar.controllers.SessionController;
import raddar.gruppen.R;
import raddar.models.QoSManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
/**
 * Temporary solution to make it possible for the user to manually 
 * enter a sipContact to call
 * @author danan612
 *
 */
public class EnterNumberView extends Activity implements OnClickListener{
	private Button call;
	private TextView enterNumber;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.enter_number_view);
		enterNumber = (TextView)this.findViewById(R.id.enter_number);
		call = (Button)this.findViewById(R.id.call);
		call.setOnClickListener(this);
	}

	public void onClick(View v) {
		if(!enterNumber.getText().equals("")){
			Intent nextIntent = new Intent(this,CallView.class);
			nextIntent.putExtra("sip","sip:" + enterNumber.getText()
					+ "@ekiga.net" );
			startActivityForResult(nextIntent,9);
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 9) {
			finish();
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		QoSManager.setCurrentActivity(this);
		QoSManager.setPowerMode();
		SessionController.getSessionController().updateConnectionImage();
	}
}
