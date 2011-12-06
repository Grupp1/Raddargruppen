package raddar.models;

import java.util.List;

import raddar.controllers.SessionController;
import raddar.views.MapUI;
import raddar.gruppen.R;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.android.maps.Overlay;

public class MapModel {

	private List<Overlay> mapOverlays;
	private Drawable d;

	private MapObjectList situationList;
	private MapObjectList resourceList;
	private MapObjectList youList;
	private MapObjectList otherList;
	private MapObjectList sosList;

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
//		if(o instanceof You&&((You) o).isSOS()){
//			o.setIcon(R.drawable.circle_red);
//		}
		

//			if(((You) o).isSOS()){
//				Log.d("soslist", o.getAddedBy());
//				if(sosList == null){
//					sosList = new MapObjectList(d, mapUI);
//				}
//				sosList.addOverlay(o);
//			}

		getList(o).addMapObject(o);
	}

	public MapObjectList getList(MapObject o){
		if(!o.getId().equals(SessionController.getUser()) && o instanceof You){
			o.setIcon(R.drawable.circle_yellow);

		}
		d = mapUI.getResources().getDrawable(o.getIcon());
	
		if(o instanceof You){
			if(o.getId().equals(SessionController.getUser())){
				if(youList == null){
					youList = new MapObjectList(d, mapUI);
				}
				return youList;
			}else{
				if(otherList == null){
					otherList = new MapObjectList(d, mapUI);
				}
				return otherList;
			}
		}
		else if(o instanceof Situation){
			if(situationList == null){
				situationList = new MapObjectList(d, mapUI);
			}
			return situationList;
		}
		else if(o instanceof Resource){
			if(resourceList == null){
				resourceList = new MapObjectList(d, mapUI);
			}
			return resourceList;
		}
		return null;
	}
	/*
	 * updateObject(MapObject) uppdaterar den MapObjectList MapObject ligger i
	 */

	public void updateObject(MapObject o){
		getList(o).addUpdateMapObject(o);
	}

	public void removeObject(MapObject o){
		getList(o).removeMapObject(o);
	}

	public List<Overlay> getMapOverlays() {
		return mapOverlays;
	}

	public void setMapOverlays(List<Overlay> mapOverlays) {
		this.mapOverlays = mapOverlays;
	}

	public void sendMessage(String user){
		mapUI.sendMessage(user);
	}
	
	public void callUser(String user){
		mapUI.callUser(user);
	}
	
}
