package raddar.views;

import java.net.InetAddress;
import java.net.UnknownHostException;

import raddar.controllers.DatabaseController;
import raddar.controllers.Sender;
import raddar.controllers.SessionController;
import raddar.enums.MessageType;
import raddar.gruppen.R;
import raddar.models.ImageMessage;
import raddar.models.QoSManager;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
	private String filePath;

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
			/*
			Message m = new TextMessage(MainView.controller.getUser(), ""
					+ destUser.getText());
			m.setSubject(subject.getText() + "");
			m.setData(messageData.getText() + "");
			try {
				new Sender(m, InetAddress.getByName(ServerInfo.SERVER_IP), ServerInfo.SERVER_PORT);
			} catch (UnknownHostException e) {

			}
			 */
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

			ImageMessage m = new ImageMessage(MessageType.IMAGE, SessionController.getUser(), ""
					+ destUsers[i], filePath);
			m.setSubject(subject.getText() + "");
			m.setFilePath(filePath);
			try {
				new Sender(m, InetAddress.getByName(raddar.enums.ServerInfo.SERVER_IP), raddar.enums.ServerInfo.SERVER_PORT);
				DatabaseController.db.addImageMessageRow(m);
				//	DatabaseController.db.addOutboxRow(m);
			//	DatabaseController.db.deleteDraftRow(m);

			} catch (UnknownHostException e) {
			//	DatabaseController.db.addDraftRow(m);
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
		switch(requestCode) { 
		case SELECT_PICTURE:
			if(resultCode == RESULT_OK){  
				Uri selectedImage = data.getData();
				String[] filePathColumn = {MediaStore.Images.Media.DATA};

				Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
				cursor.moveToFirst();
				
				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				filePath = cursor.getString(columnIndex);
				cursor.close();
				
				Bitmap yourSelectedImage = BitmapFactory.decodeFile(filePath);
				preview.setImageBitmap(yourSelectedImage);
				//bilden som ska skickas med meddelandet är yourSelectedImage

			}
		case 0:
			if(requestCode == 0){
				Bundle extras = data.getExtras();
				String temp = "";
				String[] destUsers = extras.getStringArray("contacts");
				for(int i = 0; i < destUsers.length; i++)
					temp += destUsers[i]+";";
				destUser.setText(temp);	
			}
		}

	}
	
	@Override
	public void onResume() {
		super.onResume();
		QoSManager.setCurrentActivity(this);
		QoSManager.setPowerMode();
	}

} 


