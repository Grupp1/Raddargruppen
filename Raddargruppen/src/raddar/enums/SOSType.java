package raddar.enums;

public enum SOSType {
	ALARM {
		public String toString() {
			return "alarm";
		}
	},
	CANCEL_ALARM {
		public String toString() {
			return "cancel_alarm";
		}
	}
}
