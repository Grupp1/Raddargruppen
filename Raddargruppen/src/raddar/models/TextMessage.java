package raddar.models;

import raddar.enums.MessageType;

public class TextMessage extends Message {
	
	public TextMessage(MessageType type, String srcUser, String destUser) {
		this(type, srcUser, destUser, "");
	}
	public TextMessage(String srcUser, String destUser) {
		this(MessageType.TEXT, srcUser, destUser, "");
	}

	public TextMessage(MessageType type, String srcUser, String destUser, String data) {
		this.type = type;
		this.fromUser = srcUser.trim();
		this.toUser = destUser.trim();		
		this.data = data;
		this.date = getDate();
		subject = "DEFAULT";
	}

	public String getFormattedMessage() {
		return toString();
	}
	
	@Override
	public String toString() {
		String formattedMessage = "";
		formattedMessage += Message.HEADER_TYPE + type + CRLF;
		formattedMessage += Message.HEADER_FROM + fromUser + CRLF;
		formattedMessage += Message.HEADER_TO + toUser + CRLF;
		formattedMessage += Message.HEADER_DATE + getDate() + CRLF;
		formattedMessage += Message.HEADER_SUBJECT + subject + CRLF;
		formattedMessage += CRLF + CRLF;
		formattedMessage += data;
		
		return formattedMessage;
	}
}