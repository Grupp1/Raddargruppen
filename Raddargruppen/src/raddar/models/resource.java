package raddar.models;

import android.graphics.drawable.Drawable;
import raddar.enums.ResourceStatus;

public class resource extends MapObject {

	public resource(Drawable defaultMarker) {
		super(defaultMarker);
		// TODO Auto-generated constructor stub
	}

	private ResourceStatus status;

	public ResourceStatus getStatus() {
		return status;
	}

	public void setStatus(ResourceStatus status) {
		this.status = status;
	}
	
}
