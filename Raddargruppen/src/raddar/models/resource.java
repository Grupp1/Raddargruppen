package raddar.models;

import com.google.android.maps.GeoPoint;

import raddar.enums.ResourceStatus;

public class Resource extends MapObject {
	
	private ResourceStatus status;
	
	public Resource(GeoPoint point, String title, String snippet, String ID, ResourceStatus status) {
		super(point, title, snippet, ID);
		this.status = status;
	}

	public ResourceStatus getStatus() {
		return status;
	}

	public void setStatus(ResourceStatus status) {
		this.status = status;
	}
	
}
