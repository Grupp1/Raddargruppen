package raddar.enums;

public enum NotificationType {
	CONNECT {
		public String toString() {
			return "connect";
		}
	},
	DISCONNECT {
		public String toString() {
			return "disconnect";
		}
	};
	
	public static NotificationType convert(String str) {
		if (str == null) return null;
		
		for (NotificationType mn: NotificationType.values()) {
			if (mn.toString().equalsIgnoreCase(str)) 
				return mn;			
		}
		return null;
	}
}