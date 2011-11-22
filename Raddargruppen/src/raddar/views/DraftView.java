package raddar.views;

import java.util.ArrayList;

import raddar.controllers.DatabaseController;
import raddar.enums.MessageType;
import raddar.gruppen.R;
import raddar.models.Message;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
 * Denna klass används vid utritning av drafts.
 * @author magkj501
 *
 */

public class DraftView extends ListActivity {
	private DraftAdapter ia;
	private ArrayList<Message> drafts;
	private int pos;
	
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		drafts = DatabaseController.db.getAllRowsAsArrays("drafts");

		ia = new DraftAdapter(this, R.layout.row,drafts);
		setListAdapter(ia);
		ListView lv = getListView();
		lv.setTextFilterEnabled(true);

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent nextIntent = new Intent(DraftView.this, DraftMessageView.class);
				nextIntent.putExtra("reciever", drafts.get(position).getDestUser());
				nextIntent.putExtra("subject", drafts.get(position).getSubject());
				nextIntent.putExtra("data", drafts.get(position).getData());
				nextIntent.putExtra("date", drafts.get(position).getDate());
				nextIntent.putExtra("type", drafts.get(position).getType());
				
				pos = position;
				
				startActivity(nextIntent);

			}
		});
	}
	
	public int getPosistion(){
		return pos;
	}
	
	public ArrayList<Message> getDrafts(){
		drafts = DatabaseController.db.getAllRowsAsArrays("drafts");
		return drafts;
	}

	/**
	 * Denna klass används vid utritning av drafts.
	 * @author magkj501
	 *
	 */

	private class DraftAdapter extends ArrayAdapter<Message>{

		private ArrayList<Message> items;

		public DraftAdapter(Context context, int textViewResourceId,ArrayList<Message> items) {
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
					iv.setImageResource(R.drawable.magnus);
				if (tt != null) 
					tt.setText("Mottagare: "+m.getDestUser());                            
				if(bt != null)
					bt.setText("Ämne: "+ m.getSubject());
			}			
			return v;
		}
	}
}

