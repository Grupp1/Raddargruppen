package raddar.views;

import raddar.gruppen.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import raddar.models.Contact;

public class AddContactView extends Activity implements OnClickListener{
	private EditText addContactEditText;
	private Button addContactButton;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_contact);

		addContactEditText = (EditText)this.findViewById(R.id.addContactEditText);
		addContactButton = (Button)this.findViewById(R.id.addContactButton);
		addContactButton.setOnClickListener(this);

	}

	public void onClick(View v) {

		if(v == addContactButton){
			if (addContactEditText.getText().length()==0){
				Toast.makeText(getApplicationContext(), "Fyll i namnet p� kontakten",
						Toast.LENGTH_SHORT).show();
				return;

			}
			else{
				Contact c = new Contact(addContactEditText.toString(), false);
				Toast.makeText(getApplicationContext(), "Kontakt "+addContactEditText.getText().toString()+ " sparad",
						Toast.LENGTH_SHORT).show();
				finish();
			}
		}


	}
}