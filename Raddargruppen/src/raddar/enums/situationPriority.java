package raddar.enums;

public enum SituationPriority {
	
	HIGH(){
		public String toString(){
			return "Hög";
		}
	},
	NORMAL(){
		public String toString(){
			return "Normal";
		}
	},
	LOW(){
		public String toString(){
			return "Låg";
		}
	};
}
