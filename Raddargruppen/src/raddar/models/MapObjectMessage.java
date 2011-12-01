package raddar.models;

import raddar.controllers.SessionController;
import raddar.enums.MapOperation;
import raddar.enums.MessageType;
import raddar.gruppen.R;

import com.google.gson.Gson;

public class MapObjectMessage extends Message{
	private String jsonMapObject;
	private String classString;
	private MapOperation mo;
	private String id;
	
	public MapObjectMessage(String jsonMapObject,String classString,String id,MapOperation mo){
		this.jsonMapObject = jsonMapObject;
		this.classString = classString;
		this.mo = mo;
		this.id = id;
		type = MessageType.MAPOBJECT;
		setSrcUser(SessionController.getUser());
	}
	
	public MapObjectMessage(String jsonMapObject,String classString,String id,MapOperation mo,String userName){
		this.jsonMapObject = jsonMapObject;
		this.classString = classString;
		this.mo = mo;
		this.id = id;
		type = MessageType.MAPOBJECT;
		setSrcUser(userName);
	}
	public MapObject toMapObject(){
		Class c= null ;
		try {
			c = Class.forName(classString);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}catch (NullPointerException e){
			return null;
		}
		
		MapObject mo = new Gson().fromJson(jsonMapObject, c);
		return mo;
	}
	
	
	@Override
	public String getFormattedMessage() {
		return null;
	}
	public MapOperation getMapOperation() {
		return mo;
	}
	public void setMapOperation(MapOperation m){
		this.mo = m;
	}
	public String getClassName() {
		return classString;
	}
	public String getJson(){
		return jsonMapObject;
	}
	public String getId(){
		return id;
	}
}
