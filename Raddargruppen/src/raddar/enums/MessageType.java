package raddar.enums;


/* Ett meddelandes typer */
public enum MessageType {
	NOTIFICATION("notification") {
		public String toString() {
			return "notification";
		}
	},
	TEXT("text/plain") {
		public String toString() {
			return "text/plain";
		}
	},
	IMAGE("image/jpeg") {
		public String toString() {
			return "image/jpeg";
		}
	},
	SOS("SOS") {
		public String toString() {
			return "SOS";
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