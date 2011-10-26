package raddar.models;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class MapObject {

	private String name;
	private String description;
	private int latCoord;
	private int lonCoord;
	
	private OverlayItem overlayitem;
	private GeoPoint point;
	
	private String ID;
	
	public MapObject(String name, String description, int latCoord, int lonCoord){
		this.name = name;
		this.description = name;
		this.latCoord = latCoord;
		this.lonCoord = lonCoord;
		create();
	}
	
	private void create(){
		point = new GeoPoint(latCoord, latCoord);
		overlayitem = new OverlayItem(point, name, description);
	}
	
	public OverlayItem getOverlayitem() {
		return overlayitem;
	}

}
