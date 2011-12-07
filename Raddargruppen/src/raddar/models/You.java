package raddar.models;

import raddar.controllers.SessionController;
import raddar.enums.ResourceStatus;

import com.google.android.maps.GeoPoint;

public class You extends Resource {
	private boolean isSOS;

	public You(GeoPoint point, String title, String snippet, int icon, ResourceStatus status) {
		super(point, title, snippet, icon, status);
		id = SessionController.getUser();
		isSOS = false;
	}
	public void setId(String id){
		this.id = id;
	}
	public void setSOS(boolean isSOS){
		this.isSOS = isSOS;
	}
	public boolean isSOS(){
		return isSOS;
	}
}
