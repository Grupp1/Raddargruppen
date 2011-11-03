package tddd36.server;

import raddar.enums.MessageType;
import raddar.enums.NotificationType;

public class NotificationMessage extends Message {
	
	// Message-header attributen för NotificationMessages
	public final static String HEADER_NOTIFICATION = "Notification: ";
	
	private NotificationType notification;
	
	public NotificationMessage(String fromUser, NotificationType notification) {
		this.fromUser = fromUser;
		this.notification = notification;
		this.type = MessageType.NOTIFICATION;
	}
	
	@Override
	public String getFormattedMessage() {
		return toString();
	}
	
	public NotificationType getNotification() {
		return notification;
	}

	public void setNotification(NotificationType notification) {
		this.notification = notification;
	}

	@Override
	public String toString() {
		String formattedMessage = "";
		formattedMessage += HEADER_TYPE + type.toString() + CRLF;
		formattedMessage += HEADER_FROM + fromUser + CRLF;
		formattedMessage += HEADER_NOTIFICATION + notification.toString() + CRLF;
		
		return formattedMessage;
	}
}

