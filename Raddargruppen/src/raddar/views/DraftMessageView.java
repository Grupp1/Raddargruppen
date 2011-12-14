package raddar.views;

import raddar.controllers.SessionController;
import raddar.gruppen.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class DraftMessageView extends Activity {

	public void onCreate(Bundle savedInstanceState){

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_RIGHT_ICON);
		setContentView(R.layout.draft_message);
		SessionController.titleBar(this, " - Utkast");
		final Bundle extras = getIntent().getExtras();
		TextView draftMessageDestUser = (TextView)this.findViewById(R.id.draftMessageDestUser);
		TextView draftMessageDate = (TextView)this.findViewById(R.id.draftMessageDate);
		TextView draftMessageSubject =(TextView)this.findViewById(R.id.draftMessageSubject);
		Button editDraftButton = (Button)this.findViewById(R.id.editDraftButton);
		
		editDraftButton.setOnClickListener(new View.OnClickListener() {
			
		
		/**
		 * M�ste fixas. G�r man in i utkast och trycker p� redigera utkast men sedan inte skickar det l�ggs det till 
		 * p� nytt i databasen, dvs det finns 2 likadana meddelanden i utkast. 
		 * 
		 */
		
			public void onClick(View v) {
				
				String [] items = (String[]) extras.getCharSequenceArray("message");
				Log.d("DraftMessageView", "String [] mottagen");
				Intent nextIntent = new Intent(DraftMessageView.this, SendMessageView.class);
				nextIntent.putExtra("message", items);
				startActivity(nextIntent);
				setResult(1, null);
				finish();
				
			}
		});
		
		TextView draftMessage =(TextView)this.findViewById(R.id.draftMessage);
		draftMessageDestUser.setText(extras.get("reciever").toString());
		//draftMessageDate.setText(extras.get("date").toString());
		draftMessageDate.setText("Datum ännu inte satt");
		draftMessageSubject.setText(extras.get("subject").toString());
		draftMessage.setText("\n"+extras.get("data").toString());
		
	}

}
