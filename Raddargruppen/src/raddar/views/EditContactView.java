package raddar.views;

import raddar.gruppen.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditContactView extends Activity implements OnClickListener{
	private static final int RESULT_FIRST_USER_EDIT = 0;
	private EditText editContactEditText;
	private Button editContactButton;
	int namePosition = ContactListView.namePosition;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_contact);
		editContactEditText = (EditText)this.findViewById(R.id.editContactEditText);
		editContactButton = (Button)this.findViewById(R.id.editContactButton);
		editContactButton.setOnClickListener(this);
		editContactEditText.setText(ContactListView.nameChoice);
		editContactEditText.requestFocus();
		editContactEditText.selectAll();
	}

	public void onClick(View v) {

		if(v == editContactButton){
			if (editContactEditText.getText().length()==0){
				Toast.makeText(getApplicationContext(), "Fyll i namnet på kontakten",
						Toast.LENGTH_SHORT).show();
				return;

			}
			else{
				Intent in = new Intent();
				in.putExtra("name", (editContactEditText.getText().toString()));
				in.putExtra("oldName", ContactListView.nameChoice);
				in.putExtra("position", namePosition);
				Toast.makeText(getApplicationContext(), "Redigering av kontakt "+ editContactEditText.getText().toString()+ " sparad",
						Toast.LENGTH_SHORT).show();
				setResult(RESULT_FIRST_USER_EDIT,in);
				finish();
				
			}
		}



	}
}
