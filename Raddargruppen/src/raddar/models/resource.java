package raddar.models;

import com.google.android.maps.GeoPoint;

import raddar.enums.ResourceStatus;
import raddar.gruppen.R;

public class Resource extends MapObject {
	
	private ResourceStatus status;
	
	public Resource(GeoPoint point, String title, String snippet, int icon, String id, ResourceStatus status) {
		super(point, title, snippet, icon, id);
		
		this.status = status;
	}

	public ResourceStatus getStatus() {
		return status;
	}

	public void setStatus(ResourceStatus status) {
		this.status = status;
	}
	
}
