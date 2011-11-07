package raddar.views;
import java.util.ArrayList;

import raddar.enums.MessageType;
import raddar.gruppen.R;
import raddar.models.Contact;
import raddar.models.Message;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ContactView extends ListActivity{
	private ContactAdapter ia;
	private ArrayList<Contact> contacts;
	private ArrayList<String> selected;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		contacts = new ArrayList<Contact>();
		selected = new ArrayList<String>();
		contacts.add(new Contact());
		ia = new ContactAdapter(this, R.layout.contact,contacts,selected);
		setListAdapter(ia);
		ListView lv = getListView();
		lv.setTextFilterEnabled(true);

		
	}
	public void onPause(){
		super.onPause();
		/*
		Log.d("ContactView","Adding contacts");
		Intent in = new Intent();
		in.putExtra("contacts", selected.toArray());
		setResult(RESULT_OK, in);
		finish();
		*/
	}

	private class ContactAdapter extends ArrayAdapter<Contact>{
		private ArrayList<Contact> contacts; 
		private ArrayList<String> selected;
		
		public ContactAdapter(Context context, int textViewResourceId,ArrayList<Contact> contacts,
				ArrayList<String> selected) {
			super(context, textViewResourceId,contacts);
			this.selected = selected;
			this.contacts = contacts;
		}

		public View getView(int pos, View convertView, ViewGroup parent){
			View v = convertView;
			final Contact c = contacts.get(pos);
			if(v == null){
				LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.contact, null);
				CheckBox bt = (CheckBox) v.findViewById(R.id.check);
				bt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						selected.add(c.name);
						Intent in = new Intent();
						in.putExtra("contacts", (String[])selected.toArray());
						setResult(RESULT_OK, in);
					}
				});
			}
			if(c != null){
				TextView contact = (TextView) v.findViewById(R.id.label);
				if(contact != null)
					contact.setText(c.name);
			}	
			return v;
		}
	}	
}
