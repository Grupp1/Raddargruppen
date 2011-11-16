package raddar.enums;

public enum ConnectionStatus {

	CONNECTED(){
		public String toString(){
			return "uppkopplad";
		}
	},
	
	DISCONNECTED(){
		public String toString(){
			return "avkopplad :P";
		}
	};
}
