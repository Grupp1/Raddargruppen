package raddar.models;

import raddar.enums.ResourceStatus;

public class Resource extends MapObject {
	
	public Resource(String name, String description, int latCoord, int lonCoord) {
		super(name, description, latCoord, lonCoord);
	}

	private ResourceStatus status;

	public ResourceStatus getStatus() {
		return status;
	}

	public void setStatus(ResourceStatus status) {
		this.status = status;
	}
	
}
