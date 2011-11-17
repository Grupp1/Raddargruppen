package raddar.views;

import java.util.ArrayList;

import raddar.gruppen.R;
import raddar.models.Message;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class DraftMessageView extends Activity {
	
	private Button editDraftButton;
	private DraftView draftView;
	private ArrayList<Message> drafts;

	public void onCreate(Bundle savedInstanceState){

		super.onCreate(savedInstanceState);

		setContentView(R.layout.draft_message);
		Bundle extras = getIntent().getExtras();
		TextView draftMessageDestUser = (TextView)this.findViewById(R.id.draftMessageDestUser);
		TextView draftMessageDate = (TextView)this.findViewById(R.id.draftMessageDate);
		TextView draftMessageSubject =(TextView)this.findViewById(R.id.draftMessageSubject);
		editDraftButton = (Button)this.findViewById(R.id.editDraftButton);
		
		editDraftButton.setOnClickListener(new View.OnClickListener() {
			
		
		
		
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				draftView = new DraftView();
				drafts = draftView.getDrafts();
				Message m = drafts.get(draftView.getPosistion());
				
				String [] items = {m.getDestUser().toString(), m.getSubject().toString(), m.getData().toString()};
				
				Intent nextIntent = new Intent(DraftMessageView.this, SendMessageView.class);
				nextIntent.putExtra("message", items);
				startActivity(nextIntent);
				 
				
				
//				Toast.makeText(getApplicationContext(), "Knappen funkar! ",
//						Toast.LENGTH_LONG).show();	
			}
		});
		
		TextView draftMessage =(TextView)this.findViewById(R.id.draftMessage);
		draftMessageDestUser.setText(extras.get("reciever").toString());
		draftMessageDate.setText(extras.get("date").toString());
		draftMessageSubject.setText(extras.get("subject").toString());
		draftMessage.setText("\n"+extras.get("data").toString());
		
	}

}
