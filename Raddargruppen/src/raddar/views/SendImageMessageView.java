package raddar.views;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

import raddar.controllers.SessionController;
import raddar.enums.ServerInfo;
import raddar.gruppen.R;
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
	private Bitmap yourSelectedImage;

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
		
		

		try {
			Bundle extras = getIntent().getExtras();
			String[] items = (String[]) extras.getCharSequenceArray("message");

			destUser = (EditText) this.findViewById(R.id.destUser);
			subject = (EditText) this.findViewById(R.id.subject);

			destUser.setText(items[0].toString());
			subject.setText(items[1].toString());
			sendButton = (Button) this.findViewById(R.id.sendButton);
			sendButton.setOnClickListener(this);
			destUser.setOnClickListener(this);

		} catch (Exception e) {

			Log.d("SendMessageView", e.toString());
			destUser = (EditText) this.findViewById(R.id.destUser);
			subject = (EditText) this.findViewById(R.id.subject);
			sendButton = (Button) this.findViewById(R.id.sendButton);
			sendButton.setOnClickListener(this);
			destUser.setOnClickListener(this);
		}

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
			/*
			 * Fixa nätverkskommunikationen i en separat tråd...
			 */
			new Thread(new Runnable() {
				public void run() {
					sendMessages();
				}
			}).start();

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
		String[] destUsers = destUser.getText().toString().split(";");
		String sub = subject.getText().toString();
		String from = SessionController.getUser();
		Socket so = null;
		
		//for (int j=0; j < 20; j++) {
		for(int i = 0; i < destUsers.length-1; i++){
			try {
				so = new Socket(ServerInfo.SERVER_IP, ServerInfo.SERVER_IMAGE_PORT);
				
				File file = new File(filePath);
				Log.d("SendImage...", "File size: " + file.length());
				String hashcode = "" + file.hashCode();
				Log.d("SendImage...", "Hashcode: " + hashcode);
				byte[] b = new byte[(int) file.length()];
				FileInputStream fis = new FileInputStream(file);
				BufferedInputStream bis = new BufferedInputStream(fis);

				BufferedOutputStream bos = new BufferedOutputStream(
						so.getOutputStream());
				bos.write(stringToBytes(from));
				bos.flush();
				bos.write(stringToBytes(destUsers[i]));
				bos.flush();
				bos.write(stringToBytes(sub));
				bos.flush();
				bos.write(stringToBytes(hashcode));
				bos.flush();
				bis.read(b);
				bos.write(b);
				

				bos.flush();
				bos.close();
			} catch (IOException e) {
				Log.d("SendImageMessageView.java", e.toString());
			} finally {
				try { so.close(); } catch (IOException e) {}
			}
		}
		//}
	}
	
	private byte[] stringToBytes(String str) {
		str += "****************************************";
		str = str.substring(0, 40);
		return str.getBytes();
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
				String[] filePathColumn = {MediaStore.Images.Media.DATA};

				Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
				cursor.moveToFirst();

				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				filePath = cursor.getString(columnIndex);
				cursor.close();

				yourSelectedImage = BitmapFactory.decodeFile(filePath);
				preview.setImageBitmap(yourSelectedImage);
				//bilden som ska skickas med meddelandet är yourSelectedImage
				break;
			case 0:
				if(requestCode == 0){
					Bundle extras = data.getExtras();
					String temp = "";
					String[] destUsers = extras.getStringArray("contacts");
					for(int i = 0; i < destUsers.length; i++)
						temp += destUsers[i]+"; ";
					destUser.setText(temp);	
				}
				break;
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



