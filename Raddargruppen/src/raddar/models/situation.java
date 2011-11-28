package raddar.models;

import raddar.enums.SituationPriority;

import com.google.android.maps.GeoPoint;

public class Situation extends MapObject {

	private SituationPriority priority;
	
	public Situation(GeoPoint point, String title, String snippet, int icon, SituationPriority priority) {
		super(point, title, snippet, icon);
		this.priority = priority;
	}
	
	public SituationPriority getPriority() {
		return priority;
	}
	public void setPriority(SituationPriority priority) {
		this.priority = priority;
		changeLatestUser();
	}
	
	@Override
	public void updateDescription(){
		super.updateDescription();
		String des = this.getDescription();
		des = des +"\nPrioritet: "+ getPriority().toString();
		setDescription(des);
	}
	
}
