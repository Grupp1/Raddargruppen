package raddar.views;

import java.util.List;

import raddar.gruppen.R;
import raddar.models.MapObject;
import raddar.models.MapObjectList;
import raddar.models.Resource;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

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
		this.setSatelliteView(true);


		/**
		 *  Lista över alla overlays (lager) som visas på kartan
		 */
		List<Overlay> mapOverlays = mapView.getOverlays();

		Drawable drawable;
		drawable = this.getResources().getDrawable(R.drawable.magnus);
		MapObjectList magnusList= new MapObjectList(drawable);
		
		drawable = this.getResources().getDrawable(R.drawable.niklas);
		MapObjectList niklasList = new MapObjectList(drawable);
		
		drawable = this.getResources().getDrawable(R.drawable.resource);
		MapObjectList resourceList = new MapObjectList(drawable);
		

		// Skapar en nytt object på kartan
		MapObject niklas = new MapObject(new GeoPoint(52395730, 65573080), "Niklas", "Hallo", "niklas");
		//MapObject magnus = new MapObject("Magnus", "Hej, jag heter magnus", 52395730, 65573080);
		//Resource res = new Resource("Resurs", "Hej på dig", 25394730, 65576080);
		
		// Lägger till objekten i kategorierna
		niklasList.addOverlay(niklas);
		//magnusList.addOverlay(magnus.getOverlayitem());
		//resourceList.addOverlay(res.getOverlayitem());
		
		
		// lägger på objekt "över" varandra
		mapOverlays.add(niklasList);
		mapOverlays.add(magnusList);
		mapOverlays.add(resourceList);
		
		
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
