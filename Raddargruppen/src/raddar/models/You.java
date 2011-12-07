package raddar.models;

import raddar.controllers.SessionController;
import raddar.enums.ResourceStatus;
import raddar.gruppen.R;

import com.google.android.maps.GeoPoint;

public class You extends Resource {

	private boolean SOS = false;
	
	public You(GeoPoint point, String title, String snippet, ResourceStatus status, boolean SOS) {
		super(point, title, snippet, R.drawable.circle_green, status);
		id = SessionController.getUser();
		setSOS(SOS);
	}
	
	public You(GeoPoint point, String title, String snippet, ResourceStatus status, boolean SOS, String id) {
		super(point, title, snippet, R.drawable.circle_green, status);
		this.id = id;
		setSOS(SOS);
	}
	
	public void setId(String id){
		this.id = id;
	}
	
	public void setSOS(boolean sos){
		this.SOS = sos;
		if(sos){
			setIcon(R.drawable.circle_green);
		}else{
			setIcon(R.drawable.sosicon);
		}
	}
	
	public boolean isSOS(){
		return SOS;
	}
}
