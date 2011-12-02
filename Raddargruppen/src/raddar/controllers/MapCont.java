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
	private ArrayList<MapObject>  olist;
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
		gps  = new GPSModel(m);
		//DatabaseController.db.addObserver(this);
		gps.addObserver(MapCont.this);
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
		return mapModel.getList(mo);
	}

	public MapUI getMapUI(){
		return mapUI;
	}

	public void add(MapObject o, boolean notify){

		if(mapModel != null){
			mapModel.add(o);
			if(!notify){
				o.updateData(geocoder);
				mapUI.drawNewMapObject(o);
			}else{
				Gson gson = new Gson();
				mapUI.drawNewMapObject(o);
				try{
					MapObjectMessage mom = new MapObjectMessage(gson.toJson(o),
							(o).getClass().getName(),o.getId(),MapOperation.ADD);
					new Sender(mom);
				}
				catch (UnknownHostException e) {

				}
			}
		}
		DatabaseController.db.addRow(o,notify);
	}

	public void updateObject(MapObject o,boolean notify){
		Log.d("UpdateObject","MapCont"+o.getTitle());
		if(mapModel != null){
			Log.d("UpdateObject","MapCont"+o.getTitle()+" i if-satsen");
			o.updateData(geocoder);
			if(!notify){
				mapUI.drawNewMapObject(o);
				mapModel.updateObject(o);
			}else{
				mapUI.drawNewMapObject(o);
				Gson gson = new Gson();
				try{
					MapObjectMessage mom = new MapObjectMessage(gson.toJson(o),
							(o).getClass().getName(),o.getId(),MapOperation.UPDATE);
					new Sender(mom);
				}
				catch (UnknownHostException e) {
				}
			}
		}
		DatabaseController.db.updateRow(o);
	}

	public void run() {
		olist = DatabaseController.db.getAllRowsAsArrays("map");
		for(int i = 0; i < olist.size();i++){
			MapObject o = olist.get(i);
			mapModel.add(o);
			o.updateData(geocoder);
			mapUI.drawNewMapObject(o);
		}
	}

	public void removeObject(MapObject mo,boolean notify){

		if(mapModel != null){
			mapModel.removeObject(mo);
			if(!notify){
				mapUI.drawNewMapObject(mo);
			}else{
				mapUI.drawNewMapObject(mo);
				Gson gson = new Gson();
				try{
					MapObjectMessage mom = new MapObjectMessage(gson.toJson(mo),
							(mo).getClass().getName(),mo.getId(),MapOperation.REMOVE);
					new Sender(mom);
				}
				catch (UnknownHostException e) {
				}

			}
		}
		DatabaseController.db.deleteRow(mo);
	}

	public You getYou(){
		return you;
	}

	public void update(Observable o, Object data) {
		Log.d("MapCont","Update "+o);

		if (data instanceof GeoPoint){
			if (!areYouFind){
				areYouFind = true;

				you = new You((GeoPoint)data, SessionController.getUser()+" position", "Här är "+SessionController.getUser(),
						R.drawable.circle_green, ResourceStatus.FREE);

				you.updateData(geocoder);
				olist.add(you); // databas
				add(you,true);		// karta
				mapUI.controller.animateTo(you.getPoint());
				mapUI.controller.setZoom(13);
				mapUI.follow = true;
			}
			else{
				updateObject(you,true);
				//removeObject(you, false);
			}
			you.setPoint((GeoPoint)data);	

			if (mapUI.follow){
				mapUI.controller.animateTo(you.getPoint());
			}
			mapUI.drawNewMapObject(you);
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
		if (areYouFind){

			you = new You((GeoPoint)you.getPoint(), SessionController.getUser()+" position", "Här är "+SessionController.getUser(),
					R.drawable.circle_green, ResourceStatus.FREE);

			you.updateData(geocoder);
			olist.add(you); // databas
			add(you,true);		// karta
			mapUI.controller.animateTo(you.getPoint());
			mapUI.controller.setZoom(13);
			mapUI.follow = true;
		}
	}
}
