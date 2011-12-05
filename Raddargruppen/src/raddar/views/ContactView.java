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
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

public class ContactView extends ListActivity implements OnClickListener, Observer{

	private ContactAdapter ia;
	private ArrayList<Contact> contacts;
	private ArrayList selected;
	private Button foot;

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_RIGHT_ICON);
		SessionController.titleBar(this, " - Kontaktlista");
		DatabaseController.db.addObserver(this);
		contacts = DatabaseController.db.getAllRowsAsArrays("contact");
		Collections.sort(contacts,new Comparator<Contact>(){
			public int compare(Contact object1, Contact object2) {
				return object1.getUserName().compareToIgnoreCase(object2.getUserName());
			}

		});
		selected = new ArrayList<String>();
		//	for(int i = 0;i <10;i++)
		//		contacts.add(new Contact("Peter"+i, false));
		ia = new ContactAdapter(this, R.layout.contact,contacts,selected);

		ListView lv = getListView();
		lv.setTextFilterEnabled(true);
		View footer = ((LayoutInflater)this.getSystemService
				(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.contact_footer, null, false);
		lv.addFooterView(footer);
		foot = (Button)footer.findViewById(R.id.foot);


		setListAdapter(ia);
		foot.setOnClickListener(this);
	}
	public void onClick(View v) {
		finish();
	}	

	private class ContactAdapter extends ArrayAdapter<Contact>{
		private ArrayList<Contact> contacts; 
		private ArrayList selected;

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
						Intent in = new Intent();
						if(isChecked){						
							selected.add(c.getUserName());
							String[] sel = new String[selected.size()];
							sel = (String[]) selected.toArray(sel);
							in.putExtra("contacts",sel);
							setResult(RESULT_OK, in);

						}
						else if(!isChecked){
							selected.remove(c.getUserName());
							String[] sel = new String[selected.size()];
							sel = (String[]) selected.toArray(sel);
							in.putExtra("contacts",sel);
							setResult(RESULT_OK, in);
						}
					}
				});
				if (c != null) {
					TextView tt = (TextView) v.findViewById(R.id.label);
					tt.setText(c.getUserName());
				}
			}	
			return v;
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		QoSManager.setCurrentActivity(this);
		QoSManager.setPowerMode();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		DatabaseController.db.deleteObserver(this);
	}
	public void update(Observable observable, final Object data) {
		if(data instanceof Contact){
			runOnUiThread(new Runnable(){
				public void run() {
					contacts.add((Contact)data);
					ia.notifyDataSetChanged();					
				}
			});
		}
	}
}

