package raddar.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

import raddar.models.MapModel;
import raddar.models.MapObject;
import raddar.views.MainView;
import raddar.views.MapUI;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.maps.GeoPoint;

public class MapCont implements Observer, Runnable{

	private MapModel mapModel;
	private Thread thread = new Thread(this);

	private ArrayList<MapObject>  olist;
	
	private MapUI mapUI;

	/*
	 * Skickar vidare operationer i en ny tråd till MapModel 
	 */

	public MapCont(final MapUI mapUI, ArrayList<MapObject> olist){
		this.mapUI = mapUI;
		this.olist = olist;
		mapModel = new MapModel(mapUI, this);
		thread.start();
	}

	public void add(MapObject o){
		DatabaseController.db.addRow(o);
		mapModel.add(o);
	}

	public void updateObject(MapObject o){
		mapModel.updateObject(o);
	}
	
	public void updateObject(MapObject o, String snippet){
		mapModel.updateObject(o, snippet);
	}
	
	public void run() {
		for(int i = 0; i < olist.size();i++){
			olist.get(i).updateData(new Geocoder(mapUI.getBaseContext(), Locale.getDefault()));
			mapModel.add(olist.get(i));
		}
	}
	
	public void removeObject(MapObject mo){
		mapModel.removeObject(mo);
	}
	
	public void update(Observable o, Object data) {

	}

	public String calcAdress(GeoPoint point){
		String display ="";
		Geocoder geocoder = new Geocoder(mapUI.getBaseContext(), Locale.getDefault());
		try{
			List<Address> address = geocoder.getFromLocation(point.getLatitudeE6() / 1E6, point.getLongitudeE6() / 1E6, 1);
			if(address.size() > 0){
				for(int i = 0; i<address.get(0).getMaxAddressLineIndex(); i++){
					display += address.get(0).getAddressLine(i) + "\n";
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			
		}
		return display;
	}
	
}
