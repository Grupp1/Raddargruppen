package raddar.views;

import java.io.ByteArrayOutputStream;
import java.net.UnknownHostException;

import raddar.controllers.Sender;
import raddar.controllers.SessionController;
import raddar.enums.MessageType;
import raddar.gruppen.R;
import raddar.models.QoSManager;
import raddar.models.TextMessage;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.Window;
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
	private Bitmap bitmap;
	private String before;

	private Bitmap yourSelectedImage;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_RIGHT_ICON);
		setContentView(R.layout.send_image_message);
		SessionController.titleBar(this, " - Nytt bildmeddelande");
		destUser = (EditText) this.findViewById(R.id.destUser);
		subject = (EditText) this.findViewById(R.id.subject);
		sendButton = (Button) this.findViewById(R.id.sendButton);
		choiceButton = (Button) this.findViewById(R.id.choiceButton);
		preview = (ImageView) this.findViewById(R.id.preview);
		choiceButton.setOnClickListener(this);
		sendButton.setOnClickListener(this);
		destUser.setOnClickListener(this);
		destUser.setFocusable(false);


		Bundle extras = getIntent().getExtras();
		try {
			String[] items = (String[]) extras.getCharSequenceArray("message");
			destUser.setText(items[0].toString()+";");
			subject.setText(items[1].toString());
			//messageData.setText(items[2].toString());
			//if(items.length > 3){
			//	msgId = items[3].toString();
			//	saveAsDraft = false;
			//}

		} catch (Exception e) {
			Log.d("SendMessageView", "message:"+e.toString());
		}

		//		Bundle extras = getIntent().getExtras();
		//		try {
		//			String[] items = (String[]) extras.getCharSequenceArray("message");
		//			destUser.setText(items[0].toString());
		//			subject.setText(items[1].toString());
		//		} catch (Exception e){
		//			Log.d("SendImageMessageView", "message:"+e.toString());
		//		}
		//		
		//		try{
		//			String destMapUser = extras.getString("map");
		//			destUser.setText(destMapUser);
		//		}catch (Exception e){
		//			Log.d("SendImageMessageView", "map:"+e.toString());
		//		}
		destUser.setOnClickListener(this);

		//		if(SessionController.testBitmap != null)
		//			preview.setImageBitmap(SessionController.testBitmap);

		preview.setDrawingCacheEnabled(true);
		preview.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), 
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		preview.layout(0, 0, preview.getMeasuredWidth(), preview.getMeasuredHeight()); 
		preview.buildDrawingCache(true);
	}

	public void onClick(View v) {
		if(v.equals(choiceButton)){
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT); 
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(Intent.createChooser(intent,
					"Select Picture"), SELECT_PICTURE);
			//�ppnar galleriet d�r man kan v�lja bild att bifoga

		}
		if (v.equals(sendButton)) {

			//Unders�ker om alla f�lt �r skrivna i
			//TODO: g�r nogrannare unders�kning
			String[] temp = new String[2];
			temp[0] = destUser.getText().toString().trim();
			temp[1] = subject.getText().toString().trim();

			if (temp[0].equals("")
					|| temp[1].equals("")||yourSelectedImage==null) { 
				Toast.makeText(getApplicationContext(), "Fyll i alla f�lt",
						Toast.LENGTH_SHORT).show();

				return;

				/*if (om bild inte �r tillagd){
				Toast.makeText(getApplicationContext(), "V�lj en bild",
						Toast.LENGTH_SHORT).show();
						return;
						}
				 */
			}
			/*
			 * Fixa n�tverkskommunikationen i en separat tr�d...
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
		String[] destUsers = (destUser.getText().toString()+";").split(";");
		bitmap = preview.getDrawingCache();
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
		byte[] imageBytes = stream.toByteArray();
		before = Base64.encodeToString(imageBytes,Base64.DEFAULT);

		for(int i = 0; i < destUsers.length-1;i++){
			Log.d("IMAGE",destUsers[i]);
			TextMessage m = new TextMessage(MessageType.IMAGE, SessionController.getUser(), destUsers[i], before);
			m.setSubject(subject.getText() + "");
			try {
				new Sender(m);
				//DatabaseController.db.addImageMessageRow(m);
				//DatabaseController.db.addOutboxRow(m);
				//DatabaseController.db.deleteDraftRow(m);

			} catch (UnknownHostException e) {
				//	DatabaseController.db.addDraftRow(m);

			}
		}
	}

	private byte[] stringToBytes(String str) {
		str += "****************************************";
		str = str.substring(0, 40);
		return str.getBytes();
	}


	/**
	 * Anropas n�r man valt en bild fr�n galleriet eller valt en kontakt
	 * @param requestCode 
	 * @param resultCode 0 f�r kontakt, SELECT_PICTURE f�r galleri
	 * @param data Bildens data fr�n galleriet eller
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
				if(yourSelectedImage!=null)
					yourSelectedImage.recycle();
				yourSelectedImage = BitmapFactory.decodeFile(filePath);
				preview.setImageBitmap(yourSelectedImage);
				//bilden som ska skickas med meddelandet �r yourSelectedImage
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
		SessionController.getSessionController().updateConnectionImage();
	}
} 



