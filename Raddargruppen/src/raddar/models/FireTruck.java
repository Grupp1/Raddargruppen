package raddar.models;

import raddar.enums.ResourceStatus;
import raddar.gruppen.R;

import com.google.android.maps.GeoPoint;

public class FireTruck extends Resource {

	public FireTruck(GeoPoint point, String snippet, ResourceStatus status) {
		super(point, "Brandbil", snippet, R.drawable.firetruck, status);
		// TODO Auto-generated constructor stub
	}

}
