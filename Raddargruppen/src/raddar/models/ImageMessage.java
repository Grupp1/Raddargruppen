package raddar.models;

import raddar.enums.MessageType;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageMessage extends Message {

	// H�r kommer bilden sparas efter att man l�st in den fr�n
	// telefonens minne (d�r den lagras efter att man tagit ett kort t ex)

	// private Bitmap imgBitmap;
	// f�r bildmeddelande
	protected Bitmap bitmap;
	protected String filePath;

	public ImageMessage(MessageType type, String srcUser, String destUser,
			String filePath) {
		this(type, srcUser, destUser, BitmapFactory.decodeFile(filePath));
	}

	// public ImageMessage(String srcUser, String destUser) {
	// this(MessageType.IMAGE, srcUser, destUser,
	// BitmapFactory.decodeFile(this.filePath) );
	// }

	public ImageMessage(String srcUser, String destUser, Bitmap bitmap) {
		this(MessageType.IMAGE, srcUser, destUser, bitmap);
	}

	/*
	 * Ett imagemessage inneh�ller en filePath till bilden som ska skickas. I
	 * databasen lagras filePathen. N�r bilden ska skickas h�mtas bilden fr�n
	 * telefonens minne mha filePathen. Imagemessage b�r inneh�lla bilden f�r
	 * att kunna skickas.
	 */

	public ImageMessage(MessageType type, String srcUser, String destUser,
			Bitmap bitmap) {
		this.type = type;
		this.fromUser = srcUser.trim();
		this.toUser = destUser.trim();
		this.bitmap = bitmap;
		this.filePath = filePath;
		subject = "DEFAULT";
	}

	@Override
	public String getFormattedMessage() {
		String formattedMessage = "";
		formattedMessage += Message.HEADER_TYPE + type + CRLF;
		formattedMessage += Message.HEADER_FROM + fromUser + CRLF;
		formattedMessage += Message.HEADER_TO + toUser + CRLF;
		formattedMessage += Message.HEADER_DATE + getDate() + CRLF;
		formattedMessage += Message.HEADER_SUBJECT + subject + CRLF;
		formattedMessage += CRLF + CRLF;

		return formattedMessage;
	}
	
	public String getFilePath(){
		return filePath;
	}
	
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
}
