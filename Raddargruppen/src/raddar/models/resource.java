package raddar.models;

import raddar.enums.ResourceStatus;

import com.google.android.maps.GeoPoint;



public class Resource extends MapObject {
	
	private ResourceStatus status;
	
	public Resource(GeoPoint point, String title, String snippet, int icon, ResourceStatus status) {
		super(point, title, snippet, icon);
		this.status = status;
	}

	public ResourceStatus getStatus() {
		return status;
	}

	public void setStatus(ResourceStatus status) {
		this.status = status;
		changeLatestUser();
	}
	
	@Override
	public void updateDescription(){
		super.updateDescription();
		String des ="Status: "+ getStatus().toString()+"\n";
		des += this.getDescription();
		setDescription(des);
	}
	
}
