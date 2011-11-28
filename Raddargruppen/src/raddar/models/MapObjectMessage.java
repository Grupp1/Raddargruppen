package raddar.models;

import raddar.controllers.SessionController;
import raddar.enums.MessageType;

import com.google.gson.Gson;

public class MapObjectMessage extends Message{
	private String jsonMapObject;
	private String classString;
	
	public MapObjectMessage(String jsonMapObject, String classString){
		this.jsonMapObject = jsonMapObject;
		this.classString = classString;
		type = MessageType.MAPOBJECT;
		setSrcUser(SessionController.getUser());
	}
	public MapObject toMapObject(){
		Class c= null ;
		try {
			c = Class.forName(classString);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return new Gson().fromJson(jsonMapObject, c);
	}
	
	
	@Override
	public String getFormattedMessage() {
		return null;
	}
}
