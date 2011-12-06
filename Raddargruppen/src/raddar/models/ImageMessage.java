package raddar.models;

import raddar.enums.MessageType;
import android.graphics.Bitmap;

public class ImageMessage extends Message {

	protected Bitmap bitmap;
	protected String filepath;

	public ImageMessage(String srcUser, String destUser, String path, Bitmap b) {
		this(srcUser, destUser, "<subject>", path, b);
	}
	
	public ImageMessage(String srcUser, String destUser, String subject, String path) {
		this(srcUser, destUser, subject, path, null);
	}
	
	public ImageMessage(String fromUser, String toUser, String subject, String path, Bitmap b) {
		this.type = MessageType.IMAGE;
		this.fromUser = fromUser;
		this.toUser = toUser;
		this.subject = subject;
		filepath = path;
		bitmap = b;
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
}
