package raddar.models;

import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class MapObject extends OverlayItem {

	private GeoPoint point;
	private String title;
	private String snippet;
	private int ID;
	
	public MapObject(GeoPoint point, String title, String snippet, int ID) {
		super(point, title, snippet);
		this.point = point;
		this.title = title;
		this.snippet = snippet;
		this.ID = ID;
	}
	
	public GeoPoint getPoint() {
		return point;
	}

	public void setPoint(GeoPoint point) {
		this.point = point;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSnippet() {
		return snippet;
	}

	public void setSnippet(String snippet) {
		this.snippet = snippet;
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

}
