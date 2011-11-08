package raddar.models;

import android.graphics.drawable.Drawable;
import raddar.enums.SituationPriority;

public class Situation extends MapObject {

	public Situation(Drawable defaultMarker) {
		super(defaultMarker);
		// TODO Auto-generated constructor stub
	}
	private String title;
	private String description;
	private SituationPriority priority;
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public SituationPriority getPriority() {
		return priority;
	}
	public void setPriority(SituationPriority priority) {
		this.priority = priority;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getTitle() {
		return title;
	}
	
	
}
