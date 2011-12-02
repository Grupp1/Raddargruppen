package raddar.models;

import raddar.controllers.SessionController;
import raddar.enums.ResourceStatus;

import com.google.android.maps.GeoPoint;

public class You extends Resource {

	public You(GeoPoint point, String title, String snippet, int icon, ResourceStatus status) {
		super(point, title, snippet, icon, status);
		id = SessionController.getUser();
	}
	public void setId(String id){
		this.id = id;
	}
}
