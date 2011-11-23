package raddar.models;

import raddar.enums.MessageType;
import raddar.enums.NotificationType;

public class NotificationMessage extends Message {

	// Message-header attributen för NotificationMessages
	public final static String HEADER_NOTIFICATION = "Notification: ";
	public final static String HEADER_PASSWORD = "Password: ";
	
	private String password;
	private NotificationType notification;
	

	public NotificationMessage(String fromUser, NotificationType notification) {
		this(fromUser, notification, null);
	}

	public NotificationMessage(String fromUser, NotificationType notification,
			String password) {
		this.fromUser = fromUser;
		this.notification = notification;
		this.type = MessageType.NOTIFICATION;
		this.password = password;
	}

	@Override
	public String getFormattedMessage() {
		return toString();
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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
		formattedMessage += HEADER_PASSWORD + password + CRLF;
		formattedMessage += HEADER_NOTIFICATION + notification.toString()
				+ CRLF;

		return formattedMessage;
	}
}
