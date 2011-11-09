package tddd36.server;

import raddar.enums.MessagePriority;
import raddar.enums.MessageType;

public class TextMessage extends Message {
	
	public TextMessage(MessageType type, String srcUser, String destUser) {
		this(type, srcUser, destUser, MessagePriority.NORMAL, "");
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
	}
	
	
	public String getFormattedMessage() {
		return toString();
	}
	
	@Override
	public String toString() {
		String formattedMessage = "";
		formattedMessage += Message.HEADER_TYPE + type + CRLF;
		formattedMessage += Message.HEADER_PRIO + priority + CRLF;
		formattedMessage += Message.HEADER_FROM + fromUser + CRLF;
		formattedMessage += Message.HEADER_TO + toUser + CRLF;
		formattedMessage += Message.HEADER_DATE + getFormattedDate() + CRLF;
		formattedMessage += Message.HEADER_SUBJECT + subject + CRLF;
		formattedMessage += CRLF + CRLF;
		formattedMessage += data;
		
		return formattedMessage;
	}
}