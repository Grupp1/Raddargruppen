package raddar.models;

import java.util.List;
import java.util.Observable;

import raddar.controllers.MapCont;
import raddar.views.MapUI;
import raddar.gruppen.R;
import android.graphics.drawable.Drawable;

import com.google.android.maps.Overlay;

public class MapModel extends Observable {
	
	private List<Overlay> mapOverlays;
	private MapObjectList fireList;
	private MapObjectList resourceList;
	private MapObjectList fireTruckList;
	private Drawable d;
	
	private MapUI mapUI;
	
	private MapObject test;
	
	public MapModel(MapUI mapUI, MapCont mapCont){
		this.mapUI = mapUI;
		this.addObserver(mapUI);
		this.addObserver(mapCont);
		
//		d = mapUI.getResources().getDrawable(R.drawable.firetruck);
//		resourceList = new MapObjectList(d, mapUI);
	}
	
	
	/*
	 * Alla fires utplacerade på kartan sparas här
	 */
	
	public void add(MapObject o){
		if (o instanceof Fire){
			if (fireList == null){
				d = mapUI.getResources().getDrawable(o.getIcon());
				fireList = new MapObjectList(d, mapUI);
			}
			fireList.addOverlay(o);
			this.setChanged();
			notifyObservers(fireList);
		}
		
		if(o instanceof FireTruck){
			if(fireTruckList == null){
				d = mapUI.getResources().getDrawable(R.drawable.magnus);
				fireTruckList = new MapObjectList(d, mapUI);
			}
			fireTruckList.addOverlay(o);
			this.setChanged();
			notifyObservers(fireTruckList);
		}
	
	}
	
	public MapObjectList getFireList(){
		return fireList;
	}
	
	/*
	 * Alla situationer utplacerade på kartan sparas här
	 */
	
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
