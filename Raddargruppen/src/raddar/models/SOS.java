package raddar.models;

import raddar.enums.SituationPriority;
import raddar.gruppen.R;

import com.google.android.maps.GeoPoint;

public class SOS extends Situation {

	public SOS(GeoPoint point, String snippet) {
		super(point, "SOS", snippet, R.drawable.sosicon, SituationPriority.HIGH);
	}
	
}
