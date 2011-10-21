package raddar.models;

import raddar.enums.ResourceStatus;

public class Resource extends MapObject {

	private ResourceStatus status;

	public ResourceStatus getStatus() {
		return status;
	}

	public void setStatus(ResourceStatus status) {
		this.status = status;
	}
	
}
