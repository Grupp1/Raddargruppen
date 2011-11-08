package raddar.models;

import raddar.enums.ResourceStatus;

import com.google.android.maps.GeoPoint;

public class You extends Resource {

	public You(GeoPoint point, String title, String snippet, int icon,
			String id, ResourceStatus status) {
		super(point, title, snippet, icon, id, status);
		// TODO Auto-generated constructor stub
	}

}
