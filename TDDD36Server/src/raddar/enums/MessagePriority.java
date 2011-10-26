package raddar.enums;

/* Ett meddelandes prioriteter */
public enum MessagePriority {
	NORMAL("normal") {
		public String toString() {
			return "normal";
		}
	},
	HIGH("high") {
		public String toString() {
			return "high";
		}
	};
	
	private String name;
	
	MessagePriority(String str) {
		name = str;
	}
	
	public static MessagePriority convert(String str) {
		if (str == null) return null;
		
		for (MessagePriority p: MessagePriority.values()) {
			// Om input-strängen matchar en MessagePriority, returnera denna
			if (str.equalsIgnoreCase(p.name)) 
				return p;
		}
		return null;
	}
}
