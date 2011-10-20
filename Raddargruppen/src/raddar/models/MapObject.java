package raddar.models;

import java.util.Observable;

public class MapObject extends Observable {

	private String ID;
	private long coords;
	private String name;
	
	public String getID() {
		return ID;
	}
	public void setID(String iD) {
		ID = iD;
	}
	public long getCoords() {
		return coords;
	}
	public void setCoords(long coords) {
		this.coords = coords;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}
