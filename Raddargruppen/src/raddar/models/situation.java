package raddar.models;

import raddar.enums.SituationPriority;

import com.google.android.maps.GeoPoint;

public class Situation extends MapObject {

	private SituationPriority priority;
	
	public Situation(GeoPoint point, String title, String snippet, int icon, String id, SituationPriority priority) {
		super(point, title, snippet, icon, id);
		this.priority = priority;
	}
	
	public SituationPriority getPriority() {
		return priority;
	}
	public void setPriority(SituationPriority priority) {
		this.priority = priority;
	}
	
	
}
