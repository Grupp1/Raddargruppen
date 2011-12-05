package raddar.controllers;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

import raddar.enums.MapOperation;
import raddar.enums.ResourceStatus;
import raddar.gruppen.R;
import raddar.models.GPSModel;
import raddar.models.MapModel;
import raddar.models.MapObject;
import raddar.models.MapObjectList;
import raddar.models.MapObjectMessage;
import raddar.models.You;
import raddar.views.MainView;
import raddar.views.MapUI;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.gson.Gson;

public class MapCont implements Observer, Runnable{

	private MapModel mapModel;
	public static GPSModel gps;
	private Thread thread = new Thread(this);
	//private ArrayList<MapObject>  olist;
	private MapUI mapUI;
	public boolean areYouFind = false;
	private You you;
	private Geocoder geocoder;
	/**
	 * En timer som notifierar controllern att kolla om anslutning till servern finns
	 */
	//private ConnectionTimer timer;
	//private int updateTime = 5000;

	/*
	 * Skickar vidare operationer i en ny tråd till MapModel 
	 */

	public MapCont(MainView m){
		gps  = new GPSModel(m, this);
		//DatabaseController.db.addObserver(this);
	}

	public void declareMapUI(MapUI mapUI){
		this.mapUI = mapUI;
		mapModel = new MapModel(mapUI);

		//mapModel.addObserver(this);
		geocoder = new Geocoder(mapUI.getBaseContext(), Locale.getDefault());

		if (!thread.isAlive()){
			run();
		}
		//	DatabaseController.db.addObserver(mapUI);
	}
	public MapObjectList getList(MapObject mo){
		Log.d("GET MAP OBJECT LIST",""+mo.getTitle());
		if(mapModel==null)
			return null;
		return mapModel.getList(mo);
	}

	public MapUI getMapUI(){
		return mapUI;
	}

	public void add(MapObject o, boolean sendToServer){
		Log.d("AddObject", "MapCont:"+o.getTitle());
		if(mapUI != null){
			mapModel.add(o);
			mapUI.drawNewMapObject(o);
			o.updateData(geocoder);
		}
		if(sendToServer){
			Gson gson = new Gson();
			try{
				MapObjectMessage mom = new MapObjectMessage(gson.toJson(o),
						(o).getClass().getName(),o.getId(),MapOperation.ADD);
				new Sender(mom);
			}
			catch (UnknownHostException e) {

			}
		}
		DatabaseController.db.addRow(o,sendToServer);
	}

	public void updateObject(MapObject o,boolean sendToServer){
		Log.d("UpdateObject","MapCont"+o.getTitle());
		if(mapUI!=null){
			mapUI.drawNewMapObject(o);
			mapModel.updateObject(o);
			o.updateData(geocoder);
			Log.d("HEEEEEEEEEEEEEEEEEEEEEEEEEJ", "HEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEJ");
		}
		if(sendToServer){
			Gson gson = new Gson();
			try{
				MapObjectMessage mom = new MapObjectMessage(gson.toJson(o),
						(o).getClass().getName(),o.getId(),MapOperation.UPDATE);
				new Sender(mom);
			}
			catch (UnknownHostException e) {
			}
		}

		DatabaseController.db.updateRow(o);
	}


	public void run() {
		ArrayList<MapObject> olist = DatabaseController.db.getAllRowsAsArrays("map");
		for(int i = 0; i < olist.size();i++){
			MapObject o = olist.get(i);
			mapModel.add(o);
			o.updateData(geocoder);
			mapUI.drawNewMapObject(o);
		}
		if(areYouFind){
			mapUI.controller.animateTo(you.getPoint());
			mapUI.controller.setZoom(13);
			mapUI.follow = true;
		}
	}

	public void removeObject(MapObject o,boolean notify){
		Log.d("RemoveObject", "MapCont:"+o.getTitle());
		if(mapModel != null){
			mapModel.removeObject(o);
			if(!notify){
				mapUI.drawNewMapObject(o);
			}else{
				mapUI.drawNewMapObject(o);
				Gson gson = new Gson();
				try{
					MapObjectMessage mom = new MapObjectMessage(gson.toJson(o),
							(o).getClass().getName(),o.getId(),MapOperation.REMOVE);
					new Sender(mom);
				}
				catch (UnknownHostException e) {
				}

			}
		}
		DatabaseController.db.deleteRow(o);
	}

	public You getYou(){
		return you;
	}

	public void update(Observable o, Object data) {
		Log.d("MapCont","Update "+o);

		if (data instanceof GeoPoint){
			if (!areYouFind){
				areYouFind = true;
				Log.d("YOU", o.toString());
				you = new You((GeoPoint)data, SessionController.getUser()+" position", "Här är "+SessionController.getUser(),
						R.drawable.circle_green, ResourceStatus.FREE);
				//olist.add(you); // databas, bortkommenterad fel?
				add(you,true);		// karta
			}
			else{
				updateObject(you,true);
				//removeObject(you, false);
			}
			you.setPoint((GeoPoint)data);	

			if (mapUI != null){
				mapUI.drawNewMapObject(you);
				if(mapUI.follow){
					mapUI.controller.animateTo(you.getPoint());
				}
			}

		}
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
			//e.printStackTrace();
			Log.d("Geocoder", "Service not avalible");
		}finally{

		}
		return display;
	}

	public void renewYou() {
		if (you!=null){
			//olist.remove(you);
			you = new You((GeoPoint)you.getPoint(), SessionController.getUser()+" position", "Här är "+SessionController.getUser(),
					R.drawable.circle_green, ResourceStatus.FREE);
			//olist.add(you); // databas
			updateObject(you,true);		// karta
			if (mapUI != null){
				mapUI.drawNewMapObject(you);
				if(mapUI.follow){
					mapUI.controller.animateTo(you.getPoint());
				}
			}
		}
	}
}
