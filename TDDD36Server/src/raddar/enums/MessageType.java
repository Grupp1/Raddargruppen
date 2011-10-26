package raddar.enums;


/* Ett meddelandes typer */
public enum MessageType {
	TEXT("text/plain") {
		public String toString() {
			return "text/plain";
		}
	},
	IMAGE("image/jpeg") {
		public String toString() {
			return "image/jpeg";
		}
	};
	
	private String name;
	
	MessageType(String str) {
		name = str;
	}
	
	public static MessageType convert(String str) {
		if (str == null) return null;
		
		// Om input-strängen kan matchas mot en MessagePriorty, returnera denna
		for (MessageType t: MessageType.values()) {
			if (str.equalsIgnoreCase(t.name)) 
				return t;
		}
		return null;
	}
}