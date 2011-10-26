package tddd36.server;

import raddar.enums.MessagePriority;
import raddar.enums.MessageType;

public class TextMessage extends Message {
	
	/* Själva meddelandet/texten */
	private String data;
	
	public TextMessage(MessageType type, String srcUser, String destUser) {
		this(type, srcUser, destUser, MessagePriority.NORMAL, "");
	}

	public TextMessage(MessageType type, String srcUser, String destUser, String data) {
		this(type, srcUser, destUser, MessagePriority.NORMAL, data);
	}
	
	public TextMessage(MessageType type, String srcUser, String destUser, MessagePriority priority, String data) {
		this.type = type;
		this.srcUser = srcUser;
		this.destUser = destUser;
		this.priority = priority;
		this.data = data;
	}
	
	public void setMessage(String message) {
		this.data = message;
	}
	
	public void prepend(String message) {
		this.data = message + data;
	}
	
	public void append(String message) {
		this.data += message;
	}
	
	public String getMessage() {
		return data;
	}
	
	public String getFormattedMessage() {
		return toString();
	}
	
	@Override
	public String toString() {
		String formattedMessage = "";
		formattedMessage += Message.HEADER_TYPE + type + CRLF;
		formattedMessage += Message.HEADER_PRIO + priority + CRLF;
		formattedMessage += Message.HEADER_FROM + srcUser.toLowerCase() + CRLF;
		formattedMessage += Message.HEADER_TO + destUser.toLowerCase() + CRLF;
		formattedMessage += CRLF + CRLF;
		formattedMessage += data;
		
		return formattedMessage;
	}
}