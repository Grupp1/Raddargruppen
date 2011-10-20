package raddar.misc;

import raddar.enums.MessageType;
import raddar.models.Message;
import raddar.models.TextMessage;

public class Receiver {
	
	public Message receive() {
		return new TextMessage(MessageType.TEXT, "Borche", "Alice");
	}
}