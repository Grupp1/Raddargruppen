package raddar.views;
import java.util.ArrayList;

import raddar.gruppen.R;
import raddar.models.Contact;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ContactListView extends ListActivity implements OnClickListener{
	private static final int RESULT_FIRST_USER_EDIT = 5;
	private ContactAdapter ia;
	private ArrayList<Contact> contacts;
	private int newButton = Menu.FIRST;  
	static String nameChoice;
	AdapterView.AdapterContextMenuInfo info;
	static int namePosition;


	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		contacts = MainView.db.getAllRowsAsArrays("contact");
		//	for(int i = 0;i <10;i++)
		//		contacts.add(new Contact("Peter"+i, false));
		ia = new ContactAdapter(this, R.layout.contact_list,contacts);
		ListView lv = getListView();
		lv.setTextFilterEnabled(true);
		setListAdapter(ia);
		registerForContextMenu(lv); 

	}
	public void onClick(View v) {
		finish();
	}	

	private class ContactAdapter extends ArrayAdapter<Contact>{
		private ArrayList<Contact> contacts; 

		public ContactAdapter(Context context, int textViewResourceId,ArrayList<Contact> contacts) {
			super(context, textViewResourceId,contacts);
			this.contacts = contacts;
		}

		public View getView(int pos, View convertView, ViewGroup parent){
			View v = convertView;
			final Contact c = contacts.get(pos);
			if(v == null){
				LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.contact_list, null);
			}
			if (c != null) {
				TextView tt = (TextView) v.findViewById(R.id.label);
				tt.setText(c.getUserName());
			}

			return v;
		}
	}
	public boolean onCreateOptionsMenu(Menu menu) {  
		menu.add(0,newButton,0,"Lägg till kontakt");   
		menu.setGroupVisible(1, false);  
		return super.onCreateOptionsMenu(menu);  

	}
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case Menu.FIRST:
			Intent nextIntent = new Intent(ContactListView.this, AddContactView.class); 
			startActivityForResult(nextIntent, 1);
			return true;
		default:
			return false;

		}

	}
	public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {  
		super.onCreateContextMenu(menu, v, menuInfo);  
		info = (AdapterView.AdapterContextMenuInfo)menuInfo;
		nameChoice = contacts.get(info.position).getUserName();
		namePosition = info.position;
		menu.setHeaderTitle(nameChoice);
		menu.add(0, v.getId(), 0, "Redigera kontakten");  
		menu.add(0, v.getId(), 0, "Radera kontakten");  
	}
	public boolean onContextItemSelected(MenuItem item) { 
		if(item.getTitle()=="Redigera kontakten"){
			Intent nextIntent = new Intent(ContactListView.this, EditContactView.class); 
			startActivityForResult(nextIntent, 1);
		}

		if(item.getTitle()=="Radera kontakten"){
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Är du säker på att du vill radera kontakten?")
			.setCancelable(false)
			.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					Contact c = new Contact(nameChoice, false);
					contacts.remove(info.position);
					MainView.db.deleteRow(c);
					ia.notifyDataSetChanged();
					dialog.cancel();
					Toast.makeText(getApplicationContext(), "Kontakt "+ nameChoice + " borttagen",
							Toast.LENGTH_SHORT).show();
				}
			})
			.setNegativeButton("Nej", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			});
			AlertDialog alert = builder.create();
			alert.show();
		}
		else {
			return false;
		}  
		return true;  
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == 1){
			
			if(resultCode == RESULT_OK){
				Bundle extras = data.getExtras();
				String name = extras.getString("name");
				Contact c = new Contact(name, false);	
				contacts.add(c);
				ia.notifyDataSetChanged();
				MainView.db.addRow(c);
			}
			else if(resultCode == RESULT_FIRST_USER_EDIT){
				Bundle extras = data.getExtras();
				String oldName = extras.getString("oldName");
				String name = extras.getString("name");
				int position = extras.getInt("position");
				Contact c = new Contact(oldName, false);	
				Contact nc = new Contact(name, false);	
				contacts.remove(position);
				contacts.add(nc);	
				ia.notifyDataSetChanged();
				MainView.db.updateRow(c, name);
				
			
			}
			
		}
	} 
}



