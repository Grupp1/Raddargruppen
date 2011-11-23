package raddar.enums;

public enum ConnectionStatus {

	CONNECTED(){
		public String toString(){
			return "Ansluten till servern";
		}
	},
	
	DISCONNECTED(){
		public String toString(){
			return "Ej ansluten till servern";
		}
	};
}
