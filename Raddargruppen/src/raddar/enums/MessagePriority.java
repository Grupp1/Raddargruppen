package raddar.enums;

/* Ett meddelandes prioriteter */
public enum MessagePriority {
	NORMAL {
		public String toString() {
			return "normal";
		}
	},
	HIGH {
		public String toString() {
			return "high";
		}
	}
}
