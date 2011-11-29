package raddar.views;

import java.util.ArrayList;

import raddar.controllers.DatabaseController;
import raddar.gruppen.R;
import raddar.models.Contact;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class CallContactListView extends ListActivity{

	private static final int RESULT_FIRST_USER_EDIT = 5;
	private ContactAdapter ia;
	private ArrayList<Contact> contacts;
	private int newButton = Menu.FIRST;
	static String nameChoice;
	AdapterView.AdapterContextMenuInfo info;
	static int namePosition;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		contacts = DatabaseController.db.getAllRowsAsArrays("contact");
		
		ia = new ContactAdapter(this, R.layout.call_contact_list, contacts);
		ListView lv = getListView();
		lv.setTextFilterEnabled(true);
		setListAdapter(ia);
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			/**
			 * lv är satt som en onItemClickListener
			 * Snabbt klick på en kontakt, ringer direkt
			 */
			public void onItemClick(AdapterView<?> a, View v, int position, long id) {
				Intent nextIntent = new Intent(CallContactListView.this,CallView.class);
				nextIntent.putExtra("sip","sip:" + contacts.get(position).getUserName()   
						+ "@ekiga.net" );
				startActivityForResult(nextIntent,9);
			}
		});
	}


	private class ContactAdapter extends ArrayAdapter<Contact> {
		private ArrayList<Contact> contacts;

		public ContactAdapter(Context context, int textViewResourceId,
				ArrayList<Contact> contacts) {
			super(context, textViewResourceId, contacts);
			this.contacts = contacts;
		}

		public View getView(int pos, View convertView, ViewGroup parent) {
			View v = convertView;
			final Contact c = contacts.get(pos);
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.call_contact_list, null);
			}
			if (c != null) {
				TextView tt = (TextView) v.findViewById(R.id.label);
				tt.setText(c.getUserName());
			}

			return v;
		}


	}



}




