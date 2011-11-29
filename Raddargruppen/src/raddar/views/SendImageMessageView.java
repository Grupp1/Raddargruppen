package raddar.views;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import raddar.controllers.Sender;
import raddar.controllers.SessionController;
import raddar.gruppen.R;
import raddar.models.Message;
import raddar.models.TextMessage;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class SendImageMessageView extends Activity implements OnClickListener {
	private static final int SELECT_PICTURE = 5;
	private EditText destUser;
	private EditText subject;
	private Button sendButton;
	private Button choiceButton;
	private ImageView preview;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.send_image_message);
		destUser = (EditText) this.findViewById(R.id.destUser);
		subject = (EditText) this.findViewById(R.id.subject);
		sendButton = (Button) this.findViewById(R.id.sendButton);
		choiceButton = (Button) this.findViewById(R.id.choiceButton);
		preview = (ImageView) this.findViewById(R.id.preview);
		choiceButton.setOnClickListener(this);
		sendButton.setOnClickListener(this);
		destUser.setOnClickListener(this);
		destUser.setFocusable(false);
	}

	public void onClick(View v) {
		if(v.equals(choiceButton)){
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT); 
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(Intent.createChooser(intent,
					"Select Picture"), SELECT_PICTURE);
			//öppnar galleriet där man kan välja bild att bifoga

		}
		if (v.equals(sendButton)) {
			//Undersöker om alla fält är skrivna i
			//TODO: gör nogrannare undersökning
			String[] temp = new String[2];
			temp[0] = destUser.getText().toString().trim();
			temp[1] = subject.getText().toString().trim();

			if (temp[0].equals("")
					|| temp[1].equals("")) { 
				Toast.makeText(getApplicationContext(), "Fyll i alla fält",
						Toast.LENGTH_SHORT).show();
				return;
				/*if (om bild inte är tillagd){
				Toast.makeText(getApplicationContext(), "Välj en bild",
						Toast.LENGTH_SHORT).show();
						return;
						}
				 */
			}
			sendMessages();
	
			Toast.makeText(getApplicationContext(), "Meddelande till "+destUser.getText().
					toString().trim(),
					Toast.LENGTH_SHORT).show();
			finish();
		} else if(v.equals(destUser)){
			Intent nextIntent = new Intent(SendImageMessageView.this, ContactView.class);
			startActivityForResult(nextIntent,0);
		}
	}

	private void sendMessages(){
		String[] destUsers = (destUser.getText().toString()+";").split(";");
		Log.d("number of messages",destUsers.length+"");
		for(int i = 0; i < destUsers.length;i++){


			Message m = new TextMessage(SessionController.getUser(), ""
					+ destUsers[i]);
			m.setSubject(subject.getText() + "");
			try {
				new Sender(m, InetAddress.getByName("127.0.0.1"), 6789);
			} catch (UnknownHostException e) {
			}
		}
	}
	/**
	 * Anropas när man valt en bild från galleriet eller valt en kontakt
	 * @param requestCode 
	 * @param resultCode 0 för kontakt, SELECT_PICTURE för galleri
	 * @param data Bildens data från galleriet eller
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK){
			switch(requestCode) { 
			case SELECT_PICTURE:

				Uri selectedImage = data.getData();
				Log.d("tag sendimage", "här letar jag");
				String[] filePathColumn = {MediaStore.Images.Media.DATA};

				Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
				cursor.moveToFirst();

				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				String filePath = cursor.getString(columnIndex);
				cursor.close();

				Bitmap yourSelectedImage = BitmapFactory.decodeFile(filePath);
				preview.setImageBitmap(yourSelectedImage);
				//bilden som ska skickas med meddelandet är yourSelectedImage


			case 0:
				if(requestCode == 0){
					Bundle extras = data.getExtras();
					String temp = "";
					String[] destUsers = extras.getStringArray("contacts");
					for(int i = 0; i < destUsers.length; i++)
						temp += destUsers[i]+"; ";
					destUser.setText(temp);	
				}
			}

		}

	} 
}


