package raddar.views;

import java.util.ArrayList;

import raddar.controllers.DatabaseController;
import raddar.gruppen.R;
import raddar.models.Message;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DraftMessageView extends Activity {
	
	private Button editDraftButton;

	private ArrayList<Message> drafts;

	public void onCreate(Bundle savedInstanceState){

		super.onCreate(savedInstanceState);

		setContentView(R.layout.draft_message);
		final Bundle extras = getIntent().getExtras();
		TextView draftMessageDestUser = (TextView)this.findViewById(R.id.draftMessageDestUser);
		TextView draftMessageDate = (TextView)this.findViewById(R.id.draftMessageDate);
		TextView draftMessageSubject =(TextView)this.findViewById(R.id.draftMessageSubject);
		editDraftButton = (Button)this.findViewById(R.id.editDraftButton);
		
		editDraftButton.setOnClickListener(new View.OnClickListener() {
			
		
		/**
		 * Måste fixas. Går man in i utkast och trycker på redigera utkast men sedan inte skickar det läggs det till 
		 * på nytt i databasen, dvs det finns 2 likadana meddelanden i utkast. 
		 * 
		 */
		
			public void onClick(View v) {
				
				drafts = DatabaseController.db.getAllRowsAsArrays("drafts");
				
				Message m = drafts.get(extras.getInt("position"));
				
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
