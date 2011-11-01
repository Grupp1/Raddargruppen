package raddar.views;

import java.util.ArrayList;

import raddar.gruppen.R;
import raddar.models.Inbox;
import raddar.models.Message;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class InboxView extends ListActivity {

	ArrayList<Message> inbox;
	InboxAdapter ia;
	Inbox in;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);		
		in = new Inbox();
		inbox = in.getInbox();
		ia = new InboxAdapter(this, R.layout.row,inbox);
		setListAdapter(ia);
		ListView lv = getListView();
		lv.setTextFilterEnabled(true);

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//a When clicked, show a toast with the TextView text
				Toast.makeText(getApplicationContext(), ((TextView) view).getText(),
						Toast.LENGTH_SHORT).show();				
				ia.notifyDataSetChanged();
				
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
					tt.setText("Sender: "+m.getSrcUser());                            
				if(bt != null)
					bt.setText("Subject: "+ "Subject");

			}
			return v;
		}
	}
}