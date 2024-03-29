package raddar.views;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Observable;
import java.util.Observer;

import raddar.controllers.DatabaseController;
import raddar.controllers.SessionController;
import raddar.enums.ConnectionStatus;
import raddar.gruppen.R;
import raddar.models.Contact;
import raddar.models.QoSManager;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ContactView extends ListActivity implements OnClickListener, Observer{

	private ContactAdapter ia;
	private ArrayList<Contact> contacts;
	private ArrayList selected;
	private Button foot;
	private View header;

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_RIGHT_ICON);
		SessionController.titleBar(this, " - Kontaktlista", true);

		contacts = DatabaseController.db.getAllRowsAsArrays("contact");
		DatabaseController.db.addObserver(this);
		SessionController.getSessionController().addObserver(this);

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
		header = ((LayoutInflater)this.getSystemService
				(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.contact_footer_text, null, false);
		TextView head = (TextView)header.findViewById(R.id.text_foot);
		if(SessionController.getConnectionStatus().equals(ConnectionStatus.CONNECTED)){
			head.setText("Var vänlig vänta på att kontakterna laddas ner från servern");
		}else{
			head.setText("Ingen kontakt med servern");
		}
		head.setClickable(false);
		head.setTextSize(20);
		if(contacts.size() == 0){
			lv.addHeaderView(header);
		}

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

		@Override
		public View getView(final int pos, View convertView, ViewGroup parent){
			View v = convertView;
			final Contact c = contacts.get(pos);
			
			if(v == null){
				LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.contact, null);
			}
			if (c != null) {
				TextView tt = (TextView) v.findViewById(R.id.label);
				CheckBox bt = (CheckBox) v.findViewById(R.id.check);
				bt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						Intent in = new Intent();
						if(isChecked){
							Log.d("ContactListView",c.getUserName()+" nummer "+pos);
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
				tt.setText(c.getUserName());
				if (SessionController.isOnline(c.getUserName())){
					ImageView statusImage = (ImageView) v.findViewById(R.id.statusImage);
					statusImage.setImageResource(R.drawable.online_circle_green);
				}
				else {
					ImageView statusImage = (ImageView) v.findViewById(R.id.statusImage);
					statusImage.setImageResource(R.drawable.online_circle_red);
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
		SessionController.getSessionController().updateConnectionImage();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		DatabaseController.db.deleteObserver(this);
	}
	public void update(Observable observable, final Object data) {
		final ListView lv = getListView();
		if(observable.getClass().equals(SessionController.class)){
			runOnUiThread(new Runnable(){
				public void run() {
					ia.notifyDataSetChanged();
				}
			});
		}
		else if(data == null){
			runOnUiThread(new Runnable(){
				public void run() {
					contacts.clear();
					ia.notifyDataSetChanged();
				}
			});
		}
		else if(data instanceof Contact){
			runOnUiThread(new Runnable(){
				public void run() {
					contacts.add((Contact)data);
					Collections.sort(contacts,new Comparator<Contact>(){
						public int compare(Contact object1, Contact object2) {
							return object1.getUserName().compareToIgnoreCase(object2.getUserName());
						}
					});
					if(contacts.size() == 1)
						lv.removeHeaderView(header);
					ia.notifyDataSetChanged();					
				}
			});
		}
	}
}

