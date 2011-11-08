package raddar.models;

import android.graphics.drawable.Drawable;
import raddar.enums.ResourceStatus;

public class Resource extends MapObject {

	public Resource(Drawable defaultMarker) {
		super(defaultMarker);
		// TODO Auto-generated constructor stub
	}

	private String title;
	private ResourceStatus status;

	public ResourceStatus getStatus() {
		return status;
	}

	public void setStatus(ResourceStatus status) {
		this.status = status;
	}
	
	public String getTitle(){
		return title;
	}
	 public void setTitle(String title){
		 this.title = title;
	 }
}
