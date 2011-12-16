package raddar.views;

import raddar.controllers.SessionController;
import raddar.gruppen.R;
import raddar.models.QoSManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;

public class ServiceView extends Activity implements OnClickListener{
	private ImageButton yrButton;
	private ImageButton trafikverketButton;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_RIGHT_ICON);
		setContentView(R.layout.services);
		SessionController.titleBar(this, " - Externa tjänster", true);
		yrButton = (ImageButton)this.findViewById(R.id.yrButton);
		yrButton.setOnClickListener(this);
		
		trafikverketButton = (ImageButton)this.findViewById(R.id.trafikverketButton);
		trafikverketButton.setOnClickListener(this);
		
}

	public void onClick(View v) {
		
		if(v == yrButton){
			Intent intent = new Intent(ServiceView.this, WeatherView.class);
			startActivity(intent);
		}
		
		if(v == trafikverketButton){
			MainView.theOne.viewToast("Funktionen inte implementerad");
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
