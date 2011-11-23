package raddar.models;

import raddar.enums.MessagePriority;
import raddar.enums.MessageType;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageMessage extends Message {

	// Här kommer bilden sparas efter att man läst in den från
	// telefonens minne (där den lagras efter att man tagit ett kort t ex)

	//private Bitmap imgBitmap;

	public ImageMessage(MessageType type, String srcUser, String destUser) {
		this(type, srcUser, destUser, MessagePriority.NORMAL, null);
	}

	public ImageMessage(String srcUser, String destUser) {
		this(MessageType.IMAGE, srcUser, destUser, MessagePriority.NORMAL, null);
	}

	public ImageMessage(MessageType type, String srcUser, String destUser, String filePath) {
		this(type, srcUser, destUser, MessagePriority.NORMAL, filePath);
	}

	/*
	 * Ett imagemessage innehåller en filePath till bilden som ska skickas.  
	 * I databasen lagras filePathen. När bilden ska skickas hämtas bilden från telefonens minne mha filePathen. 
	 * Imagemessage bör innehålla bilden för att kunna skickas.  
	 */
	
	public ImageMessage(MessageType type, String srcUser, String destUser, MessagePriority priority, String filePath) {
		this.type = type;
		this.fromUser = srcUser.trim();
		this.toUser = destUser.trim();
		this.priority = priority;
		this.filePath = filePath;
		subject = "DEFAULT";
	}
	
	@Override
	public String getFormattedMessage() {
		String formattedMessage = "";
		formattedMessage += Message.HEADER_TYPE + type + CRLF;
		formattedMessage += Message.HEADER_PRIO + priority + CRLF;
		formattedMessage += Message.HEADER_FROM + fromUser + CRLF;
		formattedMessage += Message.HEADER_TO + toUser + CRLF;
		formattedMessage += Message.HEADER_DATE + getFormattedDate() + CRLF;
		formattedMessage += Message.HEADER_SUBJECT + subject + CRLF;
		formattedMessage += CRLF + CRLF;

		return formattedMessage;
	}
}




