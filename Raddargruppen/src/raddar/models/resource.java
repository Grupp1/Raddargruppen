package raddar.models;

import raddar.enums.resourceStatus;

public class resource extends MapObject {

	private resourceStatus status;

	public resourceStatus getStatus() {
		return status;
	}

	public void setStatus(resourceStatus status) {
		this.status = status;
	}
	
}
