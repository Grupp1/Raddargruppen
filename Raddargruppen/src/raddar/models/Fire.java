package raddar.models;

import raddar.enums.SituationPriority;

import raddar.gruppen.R;

import com.google.android.maps.GeoPoint;

public class Fire extends Situation {

	public Fire(GeoPoint point, String snippet, String id,
			SituationPriority priority) {
		super(point, "Brand", snippet, R.drawable.fire, id, priority);
		// TODO Auto-generated constructor stub
	}

}
