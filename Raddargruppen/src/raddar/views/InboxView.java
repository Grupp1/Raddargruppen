package raddar.views;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import raddar.controllers.DatabaseController;
import raddar.enums.MessageType;
import raddar.gruppen.R;
import raddar.models.Message;
import raddar.models.QoSManager;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
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

public class InboxView extends ListActivity implements Observer{

	private InboxAdapter ia;
	private ArrayList<Message> textInbox;
	private ArrayList<Message> imageInbox;
	private ArrayList<Message> temp;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		DatabaseController.db.addObserver(this);
		temp = DatabaseController.db.getAllRowsAsArrays("message");
		//imageInbox = DatabaseController.db.getAllRowsAsArrays("imageMessage");
		
		int size = temp.size();
		textInbox = new ArrayList<Message>(size);
		
		// Vänder arraylisten så att nyaste meddelandet visas överst
		
		if(size > 1){	
			for(int i = 0; i<size; i++ ){
				textInbox.add(temp.get(size-1-i));
			}
		}else{
			textInbox = (ArrayList<Message>) temp.clone(); 
		}

		ia = new InboxAdapter(this, R.layout.row,textInbox);
		setListAdapter(ia);
		ListView lv = getListView();
		
		lv.setTextFilterEnabled(true);
		
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent nextIntent = new Intent(InboxView.this, ReadMessageView.class);
				nextIntent.putExtra("sender",textInbox.get(position).getSrcUser());
				nextIntent.putExtra("subject",textInbox.get(position).getSubject());
				nextIntent.putExtra("data",textInbox.get(position).getData());
				nextIntent.putExtra("date", textInbox.get(position).getDate());
				nextIntent.putExtra("type", textInbox.get(position).getType());
				nextIntent.putExtra("image", BitmapFactory.decodeResource(getResources(), R.drawable.inbox));
				startActivity(nextIntent);
			}
		});

		
//		Message m = new TextMessage(MessageType.convert("text/plain"),"Daniel","Daniel");
//		m.setData("HOPPAS DET FUNGERAR");
//		m.setSubject("VIKTIGT");
//		DatabaseController.db.addRow(m);
		
		//Message image = new ImageMessage(MessageType.convert("image/jpeg"),"Magnus","Magnus");
		//Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.niklas);
		//image.setImageBitmap(bitmap);
		
		//image.setSubject("TESTING TESTING");
		
		//DatabaseController.db.addImageMessageRow(image);
		
//		try {
//			new Sender (m, InetAddress.getByName("127.0.0.1"), 6789);
//		} catch (UnknownHostException e) {
//			
//		}

	}
	
	public void update(final Observable observable, final Object data) {
		runOnUiThread(new Runnable(){
			public void run(){
				textInbox.add((Message) data);
				ia.notifyDataSetChanged();
			}
		});
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		DatabaseController.db.deleteObserver(this);
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
				ImageView iv = (ImageView)v.findViewById(R.id.icon);
				if(m.getType() == MessageType.TEXT)
					iv.setImageResource(R.drawable.wordwriter);
				if(m.getType() == MessageType.IMAGE)
					iv.setImageResource(R.drawable.picturewriter);
				if (tt != null) 
					tt.setText("Avsändare: "+m.getSrcUser());                            
				if(bt != null)
					bt.setText("Ämne: "+ m.getSubject());
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
}