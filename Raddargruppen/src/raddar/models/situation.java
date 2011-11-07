package raddar.models;

import raddar.enums.SituationPriority;

import com.google.android.maps.GeoPoint;

public class Situation extends MapObject {

	private SituationPriority priority;
	
	public Situation(GeoPoint point, String title, String snippet, int ID, SituationPriority priority) {
		super(point, title, snippet, ID);
		this.priority = priority;
	}
	
	public SituationPriority getPriority() {
		return priority;
	}
	public void setPriority(SituationPriority priority) {
		this.priority = priority;
	}
	
}
