package raddar.models;

import raddar.enums.situationPriority;

public class situation extends MapObject {

	private String description;
	private situationPriority priority;
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public situationPriority getPriority() {
		return priority;
	}
	public void setPriority(situationPriority priority) {
		this.priority = priority;
	}
	
}
