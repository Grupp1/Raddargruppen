package raddar.models;

import java.util.List;
import java.util.Observable;

import raddar.views.MapUI;
import android.R;
import android.graphics.drawable.Drawable;

import com.google.android.maps.Overlay;

public class MapModel extends Observable {
	
	private List<Overlay> mapOverlays;
	private MapObjectList fireList;
	private MapObjectList resourceList;
	private Drawable d;
	private MapUI map;
	
	
	public MapModel(){
		this.addObserver(map);
	}
	
	
	/*
	 * Alla fires utplacerade på kartan sparas här
	 */
	public void addFire(Fire fire){
		
	
		fireList.addOverlay(fire);
		notifyObservers(fireList);
		
	}
	
	public MapObjectList getFireList(){
		return fireList;
	}
	
	/*
	 * Alla situationer utplacerade på kartan sparas här
	 */
	
	public void addResource(Resource resource){
		
		resourceList.addOverlay(resource);
		notifyObservers(resourceList);
		
	}
	
	public MapObjectList getResourceList(){
		return resourceList;
	}
	
	
	public List<Overlay> getMapOverlays() {
		return mapOverlays;
	}

	public void setMapOverlays(List<Overlay> mapOverlays) {
		this.mapOverlays = mapOverlays;
	}

}
