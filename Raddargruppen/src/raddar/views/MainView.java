package raddar.views;

import raddar.gruppen.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainView extends Activity implements OnClickListener {

	private Button callButton;
	private Button messageButton;
	private Button mapButton;
	private Button reportButton;
	private Button sosButton;
	private Button setupButton;
	private Button logoutButton;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		callButton = (Button)this.findViewById(R.id.callButton);
		callButton.setOnClickListener(this);
		//	public void onClick(View v){
		//		setContentView(R.layout.start);
	//		}
	//	});

		messageButton = (Button)this.findViewById(R.id.messageButton);
		messageButton.setOnClickListener(new OnClickListener(){
			public void onClick(View v){ 
			//	setContentView(R.layout.start);
				finish();
			}
		});

		mapButton = (Button)this.findViewById(R.id.mapButton);
		mapButton.setOnClickListener(new OnClickListener(){
			public void onClick(View v){ 
				setContentView(R.layout.start);
			}
		});

		reportButton = (Button)this.findViewById(R.id.reportButton);
		reportButton.setOnClickListener(new OnClickListener(){
			public void onClick(View v){ 
				setContentView(R.layout.start);
			}
		});


		sosButton = (Button)this.findViewById(R.id.sosButton);
		sosButton.setOnClickListener(new OnClickListener(){
			public void onClick(View v){ 
				setContentView(R.layout.start);
			}
		});
		
		setupButton = (Button)this.findViewById(R.id.setupButton);
		setupButton.setOnClickListener(new OnClickListener(){
			public void onClick(View v){ 
				setContentView(R.layout.start);

			}

		});
		logoutButton = (Button)this.findViewById(R.id.logoutButton);
		logoutButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v){ 
				//setContentView(R.layout.start);
				finish();
			}

		});
		//anropar loginfunktionen - går vidare till main


	}

	public void onClick(View v) {
		finish();
		if(v ==callButton){
			
		}
		
	}



}
