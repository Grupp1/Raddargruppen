package raddar.views;

import java.util.ArrayList;

import raddar.controllers.DatabaseController;
import raddar.controllers.SessionController;
import raddar.enums.MessageType;
import raddar.gruppen.R;
import raddar.models.Message;
import raddar.models.QoSManager;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Denna klass anv�nds vid utritning av drafts.
 * @author magkj501
 *
 */

public class DraftView extends ListActivity {
	private DraftAdapter ia;
	private ArrayList<Message> drafts;
	private AdapterView.AdapterContextMenuInfo info;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_RIGHT_ICON);
		SessionController.titleBar(this, " - Utkast");
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
				
				Message m = drafts.get(position);
				String [] items = {m.getDestUser().toString(), m.getSubject().toString(), m.getData().toString(),
						m.getDate()};
				for(int i=0; i<items.length; i++){
				Log.d("DraftView", items[i]);
				}
				nextIntent.putExtra("message", items);
				startActivityForResult(nextIntent,0);
			}
		});
		registerForContextMenu(lv);
	}
	
	/**
	 * Denna klass anv�nds vid utritning av drafts.
	 * @author magkj501
	 *
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		//super.onCreateContextMenu(menu, v, menuInfo);
		if(drafts.size() == 0) return;
		info = (AdapterView.AdapterContextMenuInfo) menuInfo;

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Vill du ta bort detta meddalande från utkast?")
		.setCancelable(true)
		.setPositiveButton("Ja", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int id) {
				DatabaseController.db.deleteDraftRow(drafts.get(info.position));
				drafts.remove(drafts.get(info.position));
				ia.notifyDataSetChanged();
			}
		})
		.setNegativeButton("Nej", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

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
					iv.setImageResource(R.drawable.wordwriter);
				if (tt != null) 
					tt.setText("Mottagare: "+m.getDestUser());                            
				if(bt != null)
					bt.setText("�mne: "+ m.getSubject());
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == 1)
			finish();
	}
}

