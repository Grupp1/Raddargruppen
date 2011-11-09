package raddar.models;

import java.util.List;
import java.util.Locale;
import java.util.Observable;

import raddar.controllers.MapCont;
import raddar.enums.ResourceStatus;
import raddar.enums.SituationPriority;
import raddar.views.MapUI;
import android.graphics.drawable.Drawable;
import android.location.Geocoder;

import com.google.android.maps.Overlay;

public class MapModel extends Observable {

	private List<Overlay> mapOverlays;
	private Drawable d;

	private MapObjectList fireList;
	private MapObjectList fireTruckList;
	private MapObjectList situationList;
	private MapObjectList resourceList;
	private MapObjectList youList;

	private MapUI mapUI;

	public MapModel(MapUI mapUI, MapCont mapCont){
		this.mapUI = mapUI;
		this.addObserver(mapUI);
		this.addObserver(mapCont);
	}


	/*
	 * Alla fires utplacerade p� kartan sparas h�r
	 */
	public void add(MapObject o){
		d = mapUI.getResources().getDrawable(o.getIcon());
		setChanged();
		if (o instanceof Fire){
			if (fireList == null){
				fireList = new MapObjectList(d, mapUI);
			}
			fireList.addOverlay(o);
			notifyObservers(fireList);
		}
		else if(o instanceof FireTruck){
			if(fireTruckList == null){
				fireTruckList = new MapObjectList(d, mapUI);
			}
			fireTruckList.addOverlay(o);
			notifyObservers(fireTruckList);
		}
		else if(o instanceof You){
			if(youList == null){
				youList = new MapObjectList(d, mapUI);
			}
			youList.addOverlay(o);
			notifyObservers(youList);
		}
		else if(o instanceof Situation){
			if(situationList == null){
				situationList = new MapObjectList(d, mapUI);
			}
			situationList.addOverlay(o);
			notifyObservers(situationList);
		}
		else if(o instanceof Resource){
			if(resourceList == null){
				resourceList = new MapObjectList(d, mapUI);
			}
			resourceList.addOverlay(o);
			notifyObservers(resourceList);
		}		
	}
	
	public void updateObject(MapObject o){
		setChanged();
		o.updateData(new Geocoder(mapUI.getBaseContext(), Locale.getDefault()));
		if (o instanceof Fire){
			notifyObservers(fireList);
		}
		else if(o instanceof FireTruck){
			notifyObservers(fireTruckList);
		}
		else if(o instanceof You){
			notifyObservers(youList);
		}
		else if(o instanceof Situation){
			notifyObservers(situationList);
		}
		else if(o instanceof Resource){
			notifyObservers(resourceList);
		}
	}
	
	public void updateObject(MapObject o, String snippet){
		o.setSnippet(snippet);
		updateObject(o);
	}
	

	/*
	 * Alla situationer utplacerade p� kartan sparas h�r
	 */

	public List<Overlay> getMapOverlays() {
		return mapOverlays;
	}

	public void setMapOverlays(List<Overlay> mapOverlays) {
		this.mapOverlays = mapOverlays;
	}

}
