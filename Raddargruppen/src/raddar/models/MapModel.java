package raddar.models;

import java.util.List;
import java.util.Locale;
import java.util.Observable;

import raddar.controllers.MapCont;
import raddar.controllers.SessionController;
import raddar.gruppen.R;
import raddar.views.MapUI;
import android.graphics.drawable.Drawable;
import android.location.Geocoder;
import android.util.Log;

import com.google.android.maps.Overlay;

public class MapModel {

	private List<Overlay> mapOverlays;
	private Drawable d;

	private MapObjectList fireList;
	private MapObjectList fireTruckList;
	private MapObjectList situationList;
	private MapObjectList resourceList;
	private MapObjectList youList;
	private MapObjectList otherList;

	private MapUI mapUI;

	/*
	 * I MapModel sker alla uppdateringar av kartan. Här finns MapObjectLists för alla olika typer av situationer och resurser.
	 */

	public MapModel(MapUI mapUI){
		this.mapUI = mapUI;
		//this.addObserver(mapUI);
	}


	/*
	 * add lägger till ett MapObject i rätt MapObjectList 
	 */

	public void add(MapObject o){
		if(!o.getId().equals(SessionController.getUser()) && o instanceof You){
			o.setIcon(R.drawable.circle_green);
			Log.d("IF SATS", "IF SATS");
		}
		d = mapUI.getResources().getDrawable(o.getIcon());
		if (o instanceof Fire){
			if (fireList == null){
				fireList = new MapObjectList(d, mapUI);
			}
			fireList.addOverlay(o);
		}
		else if(o instanceof FireTruck){
			if(fireTruckList == null){
				fireTruckList = new MapObjectList(d, mapUI);
			}
			fireTruckList.addOverlay(o);
		}
		else if(o instanceof You){
			if(!o.getId().equals(SessionController.getUser())){
				Log.d("otherlist", o.getAddedBy());
				if(otherList == null){
					otherList = new MapObjectList(d, mapUI);
				}
				otherList.addOverlay(o);
			}else{
				Log.d("youlist", o.getAddedBy());
				if(youList == null){
					youList = new MapObjectList(d, mapUI);
				}
				youList.addOverlay(o);
			}
		}
		else if(o instanceof Situation){
			if(situationList == null){
				situationList = new MapObjectList(d, mapUI);
			}
			situationList.addOverlay(o);
		}
		else if(o instanceof Resource){
			if(resourceList == null){
				resourceList = new MapObjectList(d, mapUI);
			}
			resourceList.addOverlay(o);
		}
	}

	public MapObjectList getList(MapObject mo){
		if (mo instanceof Fire){
			return fireList;
		}
		else if(mo instanceof FireTruck){
			return fireTruckList;
		}
		else if(mo instanceof You){
			if(mo.getId().equals(SessionController.getUser())){
				return youList;
			}else{
				return otherList;
			}
		}
		else if(mo instanceof Situation){
			return situationList;
		}
		else if(mo instanceof Resource){
			return resourceList;
		}
		return null;
	}
	/*
	 * updateObject(MapObject) uppdaterar den MapObjectList MapObject ligger i
	 */

	public void updateObject(MapObject o){
		//	o.updateData(new Geocoder(mapUI.getBaseContext(), Locale.getDefault()));
		if (o instanceof Fire){
			fireList.addUpdateMapObject(o);
		}
		else if(o instanceof FireTruck){
			fireTruckList.addUpdateMapObject(o);
		}
		else if(o instanceof You){
			youList.addUpdateMapObject(o);
		}
		else if(o instanceof Situation){
			situationList.addUpdateMapObject(o);
		}
		else if(o instanceof Resource){
			resourceList.addUpdateMapObject(o);
		}
	}

	public void removeObject(MapObject o){
		if (o instanceof Fire){
			fireList.removeMapObject(o);
		}
		else if(o instanceof FireTruck){
			fireTruckList.removeMapObject(o);
		}
		else if(o instanceof You){
			youList.removeMapObject(o);
		}
		else if(o instanceof Situation){
			situationList.removeMapObject(o);
		}
		else if(o instanceof Resource){
			resourceList.removeMapObject(o);
		}
	}

	public List<Overlay> getMapOverlays() {
		return mapOverlays;
	}

	public void setMapOverlays(List<Overlay> mapOverlays) {
		this.mapOverlays = mapOverlays;
	}

}
