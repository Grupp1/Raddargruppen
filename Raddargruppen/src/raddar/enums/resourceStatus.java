package raddar.enums;

public enum ResourceStatus {

	BUSY(){
		public String toString(){
			return "Upptagen";
		}
	},
	FREE(){
		public String toString(){
			return "Ledig";
		}
	}
	
}
