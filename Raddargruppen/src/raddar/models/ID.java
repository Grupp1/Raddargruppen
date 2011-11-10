package raddar.models;

public class ID {

	String user;
	int latitude;
	int longitud;
	String time;
	
	public ID(String user, int latitude, int longitud, String time){
		this.user = user;
		this.latitude = latitude;
		this.longitud = longitud;
		this.time = time;
	}
	
	public String generateID(){
		String sep = ";";
		String temp = user+sep+latitude+sep+longitud+sep+time;
		return temp;
	}
	
}
