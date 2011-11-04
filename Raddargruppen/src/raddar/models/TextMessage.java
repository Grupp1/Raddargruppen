package raddar.models;

import raddar.enums.MessagePriority;
import raddar.enums.MessageType;

public class TextMessage extends Message {
	
	public TextMessage(MessageType type, String srcUser, String destUser) {
		this(type, srcUser, destUser, MessagePriority.NORMAL, "");
		subject = "DEFAULT"; 
	}
	public TextMessage(String srcUser, String destUser) {
		this(MessageType.TEXT, srcUser, destUser, MessagePriority.NORMAL, "");
		subject = "DEFAULT"; 
	}

	public TextMessage(MessageType type, String srcUser, String destUser, String data) {
		this(type, srcUser, destUser, MessagePriority.NORMAL, data);
	}
	
	public TextMessage(MessageType type, String srcUser, String destUser, MessagePriority priority, String data) {
		this.type = type;
		this.fromUser = srcUser;
		this.toUser = destUser;
		this.priority = priority;
		this.data = data;
		subject = "DEFAULT";
	}
	
	
	public String getFormattedMessage() {
		return toString();
	}
	
	@Override
	public String toString() {
		String formattedMessage = "";
		formattedMessage += Message.HEADER_TYPE + type + CRLF;
		formattedMessage += Message.HEADER_PRIO + priority + CRLF;
		formattedMessage += Message.HEADER_FROM + fromUser.toLowerCase() + CRLF;
		formattedMessage += Message.HEADER_TO + toUser.toLowerCase() + CRLF;
		formattedMessage += Message.HEADER_DATE + getFormattedDate() + CRLF;
		formattedMessage += Message.HEADER_SUBJECT + subject + CRLF;
		formattedMessage += CRLF + CRLF;
		formattedMessage += data;
		
		return formattedMessage;
	}
}