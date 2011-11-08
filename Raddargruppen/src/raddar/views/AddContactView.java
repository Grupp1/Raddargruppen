package raddar.views;

import raddar.gruppen.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class AddContactView extends Activity implements OnClickListener{
	private EditText addContactEditText;
	private Button addContactButton;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_contact);
		
		
		addContactButton = (Button)this.findViewById(R.id.addContactButton);
		addContactButton.setOnClickListener(this);
		
}

	public void onClick(View v) {
		
		if(v == addContactButton){
			//if lenght=!0
			finish();
		}
		
		
		
	}
}
