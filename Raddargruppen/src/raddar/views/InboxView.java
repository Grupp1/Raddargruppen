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
import raddar.models.ClientDatabaseManager;
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

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();
		MainView.controller.addObserverToInbox(this);
		inbox = MainView.controller.getInbox();
		//MainView.controller.addObserverToInbox(this);
		//inbox = MainView.controller.getInbox();
		ia = new InboxAdapter(this, R.layout.row,inbox);
		setListAdapter(ia);
		ListView lv = getListView();
		lv.setTextFilterEnabled(true);
		
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent nextIntent = new Intent(InboxView.this, ReadMessageView.class);
				nextIntent.putExtra("sender",inbox.get(position).getSrcUser());
				nextIntent.putExtra("subject",inbox.get(position).getSubject());
				nextIntent.putExtra("data",inbox.get(position).getData());
				nextIntent.putExtra("date", inbox.get(position).getDate());
				nextIntent.putExtra("type", inbox.get(position).getType());
				startActivity(nextIntent);

			}
		});
		
		Message m = new TextMessage(MessageType.convert("text/plain"),"Daniel","Daniel");
		m.setData("HOPPAS DET FUNGERAR");
		m.setSubject("VIKTIGT");
		try {
			new Sender (m, InetAddress.getByName("127.0.0.1"), 6789);
		} catch (UnknownHostException e) {
			
		}
	}
	
	public void update(final Observable observable, Object data) {
		runOnUiThread(new Runnable(){
			public void run(){
				Log.d("NEJ","hmm");
				inbox = ((ClientDatabaseManager)observable).getAllRowsAsArrays();
				ia.notifyDataSetChanged();				
				//Toast.makeText(getApplicationContext(), "Meddelande från "+inbox.get(inbox.size()-1).getSrcUser()
				//		,Toast.LENGTH_LONG).show();
			}
		});
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
					bt.setText("Ämne: "+ m.getSubject());

			}
			return v;
		}
	}
}