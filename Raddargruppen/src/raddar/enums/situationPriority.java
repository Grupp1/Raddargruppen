package raddar.enums;

public enum SituationPriority {
	
	HIGH("high"){
		public String toString(){
			return "high";
		}
	},
	NORMAL("normal"){
		public String toString(){
			return "normal";
		}
	},
	LOW("low"){
		public String toString(){
			return "low";
		}
	};
	
	private String priority;
	
	SituationPriority(String s){
		priority = s;
	}

}
