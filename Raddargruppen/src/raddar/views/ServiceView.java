package raddar.views;

import raddar.gruppen.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class ServiceView extends Activity implements OnClickListener{
	private ImageButton yrButton;
	private ImageButton trafikverketButton;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.services);
		
		yrButton = (ImageButton)this.findViewById(R.id.yrButton);
		yrButton.setOnClickListener(this);
		
		trafikverketButton = (ImageButton)this.findViewById(R.id.trafikverketButton);
		trafikverketButton.setOnClickListener(this);
		
}

	public void onClick(View v) {
		
		if(v == yrButton){
			finish();
		}
		
		if(v == trafikverketButton){
			finish();
		}
		
	}
}
