package raddar.views;

import raddar.controllers.SessionController;
import raddar.gruppen.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DraftMessageView extends Activity {

	public void onCreate(Bundle savedInstanceState){

		super.onCreate(savedInstanceState);

		setContentView(R.layout.draft_message);
		SessionController.titleBar(this, " - Utkast");
		final Bundle extras = getIntent().getExtras();
		TextView draftMessageDestUser = (TextView)this.findViewById(R.id.draftMessageDestUser);
		TextView draftMessageDate = (TextView)this.findViewById(R.id.draftMessageDate);
		TextView draftMessageSubject =(TextView)this.findViewById(R.id.draftMessageSubject);
		Button editDraftButton = (Button)this.findViewById(R.id.editDraftButton);
		
		editDraftButton.setOnClickListener(new View.OnClickListener() {
			
		
		/**
		 * Måste fixas. Går man in i utkast och trycker på redigera utkast men sedan inte skickar det läggs det till 
		 * på nytt i databasen, dvs det finns 2 likadana meddelanden i utkast. 
		 * 
		 */
		
			public void onClick(View v) {
				
				String [] items = (String[]) extras.getCharSequenceArray("message");
				Log.d("DraftMessageView", "String [] mottagen");
				Intent nextIntent = new Intent(DraftMessageView.this, SendMessageView.class);
				nextIntent.putExtra("message", items);
				nextIntent.putExtra("isDraft", true);
				startActivity(nextIntent);
				 
				finish();
				
			}
		});
		
		TextView draftMessage =(TextView)this.findViewById(R.id.draftMessage);
		draftMessageDestUser.setText(extras.get("reciever").toString());
		draftMessageDate.setText(extras.get("date").toString());
		draftMessageSubject.setText(extras.get("subject").toString());
		draftMessage.setText("\n"+extras.get("data").toString());
		
	}

}
