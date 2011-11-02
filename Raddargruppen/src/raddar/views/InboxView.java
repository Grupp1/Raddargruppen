package raddar.views;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import raddar.controllers.InternalComManager;
import raddar.controllers.Sender;
import raddar.enums.MessageType;
import raddar.gruppen.R;
import raddar.models.Inbox;
import raddar.models.Message;
import raddar.models.TextMessage;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class InboxView extends ListActivity implements Observer{

	private ArrayList<Message> inbox;
	private InboxAdapter ia;
	InternalComManager controller;

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);	
		controller = new InternalComManager();
		controller.addObserverToInbox(this);
		inbox = controller.getInbox();
		ia = new InboxAdapter(this, R.layout.row,inbox);
		setListAdapter(ia);
		ListView lv = getListView();
		lv.setTextFilterEnabled(true);
		
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent nextIntent = new Intent(InboxView.this, ReadMessageView.class);
				nextIntent.putExtra("sender",inbox.get(position).getSrcUser());
				nextIntent.putExtra("subject","Ämnet");
				nextIntent.putExtra("data",inbox.get(position).getData());
				nextIntent.putExtra("date", "2011-11-02");
				nextIntent.putExtra("type", inbox.get(position).getType());
				startActivity(nextIntent);

			}
		});
		Message m = new TextMessage(MessageType.convert("text/plain"),"Daniel","Daniel");
		m.setData("HOPPAS DET FUNGERAR");
		try {
			new Sender (m, InetAddress.getByName("127.0.0.1"), 6789);
		} catch (UnknownHostException e) {
			
		}
	}
	
	public void update(Observable observable, Object data) {
		inbox = ((Inbox)observable).getInbox();
		ia.notifyDataSetChanged();
	}

	private class InboxAdapter extends ArrayAdapter<Message>{
		private ArrayList<Message> items;

		public InboxAdapter(Context context, int textViewResourceId,ArrayList<Message> items) {
			super(context, textViewResourceId,items);
			this.items = items;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.row, null);
			}
			Message m = items.get(position);
			if (m != null) {
				TextView tt = (TextView) v.findViewById(R.id.toptext);
				TextView bt = (TextView) v.findViewById(R.id.bottomtext);
				if (tt != null) 
					tt.setText("Avsändare: "+m.getSrcUser());                            
				if(bt != null)
					bt.setText("Ämne: "+ "Subject");

			}
			return v;
		}
	}
}