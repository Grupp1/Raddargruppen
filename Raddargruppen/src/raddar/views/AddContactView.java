package raddar.views;

import java.util.Map;

import raddar.gruppen.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import raddar.models.Contact;

public class AddContactView extends Activity implements OnClickListener{
	private EditText addContactEditText;
	private Button addContactButton;


	private int newButton = Menu.FIRST + 1;  
	private int otherButton = Menu.FIRST + 2;  

	private int group2Id = 2; 

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_contact);


		addContactEditText = (EditText)this.findViewById(R.id.addContactEditText);
		addContactButton = (Button)this.findViewById(R.id.addContactButton);
		addContactButton.setOnClickListener(this);

	}
	public boolean onCreateOptionsMenu(Menu menu) {  
		menu.add(0,newButton,0,"Lägg till kontakt");  
		menu.add(0,otherButton,0,"Annan knapp");  
		menu.setGroupVisible(1, false);  
		return super.onCreateOptionsMenu(menu);  

	}
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case Menu.FIRST + 1:
			Intent nextIntent = new Intent(AddContactView.this, Map.class); //Byt plats på views, testar
			startActivity(nextIntent);
			return true;
		case Menu.FIRST + 2:
			finish();
			return true;
		default:
			return false;

		}
		
	}
	//Kolla på detta http://www.roseindia.net/java/beginners/SwitchExample.shtml
	
	//detta ska in i kontaktlistan

	public void onClick(View v) {

		if(v == addContactButton){
			if (addContactEditText.getText().length()==0){
				Toast.makeText(getApplicationContext(), "Fyll i namnet på kontakten",
						Toast.LENGTH_SHORT).show();
				return;

			}
			else{
				Contact c = new Contact(addContactEditText.toString(), false);
				MainView.db.addRow(c);
				Toast.makeText(getApplicationContext(), "Kontakt "+addContactEditText.getText().toString()+ " sparad",
						Toast.LENGTH_SHORT).show();
				finish();
			}
		}



	}
}
