package raddar.models;

import raddar.enums.SituationPriority;

public class Situation extends MapObject {

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
	
}
