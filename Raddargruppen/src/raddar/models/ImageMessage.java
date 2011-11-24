package raddar.models;

import raddar.enums.MessageType;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageMessage extends Message {

	// Här kommer bilden sparas efter att man läst in den från
	// telefonens minne (där den lagras efter att man tagit ett kort t ex)

	// private Bitmap imgBitmap;
	// för bildmeddelande
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
	 * Ett imagemessage innehåller en filePath till bilden som ska skickas. I
	 * databasen lagras filePathen. När bilden ska skickas hämtas bilden från
	 * telefonens minne mha filePathen. Imagemessage bör innehålla bilden för
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
