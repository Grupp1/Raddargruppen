package raddar.enums;


/* Ett meddelandes typer */
public enum MessageType {
	TEXT {
		public String toString() {
			return "text/plain";
		}
	},
	IMAGE {
		public String toString() {
			return "image/jpeg";
		}
	}
}