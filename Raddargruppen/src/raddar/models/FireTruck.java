package raddar.models;

import raddar.enums.ResourceStatus;
import raddar.gruppen.R;

public class FireTruck extends Resource {

	public FireTruck(GeoPoint point, String snippet, ResourceStatus status) {
		super(point, "Brandbil", snippet, R.drawable.firetruck, status);
		// TODO Auto-generated constructor stub
	}

}
