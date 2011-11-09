package raddar.enums;

public enum SituationPriority {
	
	HIGH(){
		public String toString(){
			return "H�g";
		}
	},
	NORMAL(){
		public String toString(){
			return "Normal";
		}
	},
	LOW(){
		public String toString(){
			return "L�g";
		}
	};
}
