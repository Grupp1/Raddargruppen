package raddar.views;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Observable;
import java.util.Observer;

import raddar.controllers.DatabaseController;
import raddar.controllers.SessionController;
import raddar.gruppen.R;
import raddar.models.Contact;
import raddar.models.QoSManager;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class CallContactListView extends ListActivity implements Observer{

	private static final int RESULT_FIRST_USER_EDIT = 5;
	private ContactAdapter ia;
	private ArrayList<Contact> contacts;
	private int newButton = Menu.FIRST;
	static String nameChoice;
	AdapterView.AdapterContextMenuInfo info;
	static int namePosition;
	private View footer;
	private TextView foot;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_RIGHT_ICON);
		SessionController.titleBar(this, " - Samtal");

		contacts = SessionController.getOnlineContacts();
		SessionController.getSessionController().addObserver(this);
		Collections.sort(contacts,new Comparator<Contact>(){
			public int compare(Contact object1, Contact object2) {
				return object1.getUserName().compareToIgnoreCase(object2.getUserName());
			}
		});
		ia = new ContactAdapter(this, R.layout.call_contact_list, contacts);
		ListView lv = getListView();
		lv.setTextFilterEnabled(true);

		lv.setOnItemClickListener(new OnItemClickListener() {

			/**
			 * lv �r satt som en onItemClickListener
			 * Snabbt klick p� en kontakt, ringer direkt
			 */
			public void onItemClick(AdapterView<?> a, View v, int position, long id) {
				if(contacts.size() == 0) return;
				Intent nextIntent = new Intent(CallContactListView.this,CallView.class);
				nextIntent.putExtra("sip","sip:" + contacts.get(position).getUserName()   
						+ "@ekiga.net" );
				nextIntent.putExtra("dstUser", contacts.get(position).getUserName());
				startActivityForResult(nextIntent,9);
			}
		});
		if(contacts.size() == 0){
			footer = ((LayoutInflater)this.getSystemService
					(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.contact_footer_text, null, false);
			lv.addFooterView(footer);
			foot = (TextView)footer.findViewById(R.id.text_foot);
			foot.setText("Ingen är online för tillfället!");
		}
		setListAdapter(ia);
	}


	private class ContactAdapter extends ArrayAdapter<Contact> {
		private ArrayList<Contact> contacts;

		public ContactAdapter(Context context, int textViewResourceId,
				ArrayList<Contact> contacts) {
			super(context, textViewResourceId, contacts);
			this.contacts = contacts;
		}

		public View getView(int pos, View convertView, ViewGroup parent) {
			//TODO Programmet ritar nu ut alla kontaktr utan att fylla i namnen. Det ska inte ens rita ut dem!

			View v = convertView;
			final Contact c = contacts.get(pos);
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.call_contact_list, null);
			}
			if ((c != null) /* && SessionController.isOnline(c.getUserName())*/) {
				TextView tt = (TextView) v.findViewById(R.id.label);
				tt.setText(c.getUserName());
			}

			return v;
		}



	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		DatabaseController.db.deleteObserver(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		QoSManager.setCurrentActivity(this);
		QoSManager.setPowerMode();
	}

	public void update(Observable observable, final Object data) {
		if(data instanceof Contact){
			runOnUiThread(new Runnable(){
				public void run() {
					Contact c = (Contact) data;
					if(c.isgroup())
						contacts.add(c);
					else{
						Log.d("ONLINEUSERS",contacts.remove(c)+"");
					}
					Collections.sort(contacts,new Comparator<Contact>(){
						public int compare(Contact object1, Contact object2) {
							return object1.getUserName().compareToIgnoreCase(object2.getUserName());
						}
					});
					ListView lv = getListView();
					if(contacts.size() == 0){
						lv.addFooterView(footer);
						foot.setText("Ingen är online för tillfället!");
					}
					else{
						lv.removeFooterView(footer);
					}
					ia.notifyDataSetChanged();					
				}
			});
		}
	}
}




