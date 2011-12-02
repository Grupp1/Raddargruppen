package raddar.views;

import java.util.ArrayList;

import raddar.controllers.DatabaseController;
import raddar.controllers.SessionController;
import raddar.enums.MessageType;
import raddar.gruppen.R;
import raddar.models.Message;
import raddar.models.QoSManager;
import android.app.ListActivity;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Denna klass tar hand om utritningen av outboxen.   
 * @author magkj501
 *
 */

public class OutBoxView extends ListActivity {

	private OutboxAdapter ia;
	private ArrayList<Message> outbox;
	private ArrayList<Message> temp;

	/**
	 * 
	 */

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		SessionController.titleBar(this, " - Utkorg");

		temp = DatabaseController.db.getAllRowsAsArrays("outbox");
		int size = temp.size();
		outbox = new ArrayList<Message>(size);
		
		// Vänder arraylisten så att nyaste meddelandet visas överst
		
		if(size > 1){
			for(int i = 0; i<size; i++ ){
				outbox.add(temp.get(size-1-i));
			}
		}else{
			outbox = (ArrayList<Message>) temp.clone(); 
		}
		ia = new OutboxAdapter(this, R.layout.row,outbox);
		setListAdapter(ia);
		ListView lv = getListView();
		lv.setTextFilterEnabled(true);

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent nextIntent = new Intent(OutBoxView.this, SentMessageView.class);
				nextIntent.putExtra("reciever",outbox.get(position).getDestUser());
				Log.e("Dest user", outbox.get(position).getDestUser());
				nextIntent.putExtra("subject",outbox.get(position).getSubject());
				nextIntent.putExtra("data",outbox.get(position).getData());
				nextIntent.putExtra("date", outbox.get(position).getDate());
				nextIntent.putExtra("type", outbox.get(position).getType());
				startActivity(nextIntent);

			}
		});
	}

	/**
	 * Denna klass används vid utritning av outboxen.
	 * @author magkj501
	 *
	 */

	private class OutboxAdapter extends ArrayAdapter<Message>{

		private ArrayList<Message> items;

		public OutboxAdapter(Context context, int textViewResourceId,ArrayList<Message> items) {
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
				ImageView iv = (ImageView) v.findViewById(R.id.icon);
				if(m.getType() == MessageType.TEXT)
					iv.setImageResource(R.drawable.wordwriter);
				if (tt != null) 
					tt.setText("Mottagare: "+m.getDestUser());                            
				if(bt != null)
					bt.setText("Ämne: "+ m.getSubject());
			}			
			return v;
		}
		
	}	@Override
	public void onResume() {
		super.onResume();
		QoSManager.setCurrentActivity(this);
		QoSManager.setPowerMode();
	}


}
