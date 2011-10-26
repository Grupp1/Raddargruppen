package raddar.views;

import java.util.List;

import raddar.gruppen.R;
import raddar.models.MapObject;
import raddar.models.MapObjectList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class Map extends MapActivity {

	private MapView mapView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.maps);

		Button close;
		close = (Button)this.findViewById(R.id.button_close);
		close.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});

		
		//MapView mapView = ((MapView)findViewById(R.id.mapview), "0b1qi7XBfQqm8teK24blL1Hhnfhqc9iOFejhYUw");
		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		//this.setSatelliteView(true);
		
		
		
		List<Overlay> mapOverlays = mapView.getOverlays();
		Drawable drawable = this.getResources().getDrawable(R.drawable.icon);
		MapObjectList itemizedoverlay = new MapObjectList(drawable);
		
		
		
//		GeoPoint point = new GeoPoint(58395730,15573080);
//		OverlayItem overlayitem = new OverlayItem(point, "LIU", "liu");
		
		MapObject magnus = new MapObject("Magnus", "Hej, jag heter magnus", 58395730, 15573080);
		
//		GeoPoint point2 = new GeoPoint(35410000, 139460000);
//		OverlayItem overlayitem2 = new OverlayItem(point2, "Sekai, konichiwa!", "I'm in Japan!");
		
		itemizedoverlay.addOverlay(magnus.getOverlayitem());
		//itemizedoverlay.addOverlay(overlayitem);
		//itemizedoverlay.addOverlay(overlayitem2);
		
		mapOverlays.add(itemizedoverlay);
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setSatelliteView(boolean on){
		mapView.setSatellite(on);
	}
	
	public void setTrafficView(boolean on){
		mapView.setTraffic(on);
	}
	
}
