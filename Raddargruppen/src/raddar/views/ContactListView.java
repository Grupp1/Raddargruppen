package raddar.views;

import java.util.ArrayList;

import raddar.controllers.DatabaseController;
import raddar.controllers.SessionController;
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

public class ContactListView extends ListActivity implements OnClickListener {
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
		// for(int i = 0;i <10;i++)
		// contacts.add(new Contact("Peter"+i, false));
		ia = new ContactAdapter(this, R.layout.contact_list, contacts);
		ListView lv = getListView();
		lv.setTextFilterEnabled(true);
		setListAdapter(ia);
		registerForContextMenu(lv);

	}

	public void onClick(View v) {
		finish();
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
				v = vi.inflate(R.layout.contact_list, null);
			}
			if (c != null) {
				TextView tt = (TextView) v.findViewById(R.id.label);
				tt.setText(c.getUserName());
			}

			return v;
		}


	}

	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		nameChoice = contacts.get(info.position).getUserName();
		namePosition = info.position;
		menu.setHeaderTitle(nameChoice);
		menu.add(0, v.getId(), 0, "Ring");
		menu.add(0, v.getId(), 0, "Skicka textmeddelande");
		menu.add(0, v.getId(), 0, "Skicka bildmeddelande");
	}

	public boolean onContextItemSelected(MenuItem item) {
		if (item.getTitle() == "Ring") {
			Intent nextIntent = new Intent(this,CallView.class);
			nextIntent.putExtra("sip","sip:" + contacts.get(info.position).getSipUsr()
					+ "@ekiga.net" );
			startActivityForResult(nextIntent,9);
			
		}else if (item.getTitle() == "Skicka textmeddelande") {
				Intent nextIntent = new Intent(this,SendMessageView.class);
				String[] items = new String[3];
				items[0] = contacts.get(info.position).getUserName();
				items[1] = "";
				items[2] = "";
				nextIntent.putExtra("message",items);
				startActivityForResult(nextIntent,9);
				
		}else if (item.getTitle() == "Skicka bildmeddelande") {
			Intent nextIntent = new Intent(this,SendImageMessageView.class);
			String[] items = new String[2];
			items[0] = contacts.get(info.position).getUserName();
			items[1] = "";
			nextIntent.putExtra("message",items);
			startActivityForResult(nextIntent,9);
		} else {
			return false;
		}
		return true;
	}



	
}
