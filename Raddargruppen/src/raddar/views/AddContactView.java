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
import android.widget.EditText;
import android.widget.Toast;

public class AddContactView extends Activity implements OnClickListener{
	private EditText addContactEditText;
	private Button addContactButton;
	private Button cancelButton;


	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_contact);

		addContactEditText = (EditText)this.findViewById(R.id.addContactEditText);
		addContactButton = (Button)this.findViewById(R.id.addContactButton);
		addContactButton.setOnClickListener(this);
		cancelButton = (Button)this.findViewById(R.id.addContactCancel);
		cancelButton.setOnClickListener(this);
	

	}


	public void onClick(View v) {

		if(v == addContactButton){
			if (addContactEditText.getText().length()==0){
				Toast.makeText(getApplicationContext(), "Fyll i namnet på kontakten",
						Toast.LENGTH_SHORT).show();
				return;

			}
			else{
				Intent in = new Intent();
				in.putExtra("name", (addContactEditText.getText().toString()));
				Toast.makeText(getApplicationContext(), "Kontakt "+addContactEditText.getText().toString()+ " sparad",
						Toast.LENGTH_SHORT).show();
				setResult(RESULT_OK,in);
				finish();
			}
		}
		if(v == cancelButton){
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
