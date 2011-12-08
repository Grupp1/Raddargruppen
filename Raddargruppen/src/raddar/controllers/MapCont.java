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
	public boolean follow = false;
	public static GPSModel gps;
	private Thread thread = new Thread(this);
	//private ArrayList<MapObject>  olist;
	private MapUI mapUI;
	public boolean areYouFind = false;
	private You you;
	private Geocoder geocoder;

	/*
	 * Skickar vidare operationer i en ny tr�d till MapModel 
	 */

	public MapCont(MainView m){
		gps  = new GPSModel(m, this);
		//DatabaseController.db.addObserver(this);
	}

	public void declareMapUI(MapUI mapUI){
		this.mapUI = mapUI;
		mapModel = new MapModel(mapUI);
		geocoder = new Geocoder(mapUI.getBaseContext(), Locale.getDefault());

		if (!thread.isAlive()){
			run();
		}
	}
	public MapObjectList getList(MapObject mo){
		Log.d("GET MAP OBJECT LIST",""+mo.getTitle());
//		if(mapModel==null)
//			return null;
		return mapModel.getList(mo);
	}

	public MapUI getMapUI(){
		return mapUI;
	}

	public void add(MapObject o, boolean sendToServer){
		Log.d("AddObject", "MapCont:"+o.getTitle());
		if(mapUI != null){
			mapModel.add(o);
			o.updateData(geocoder);
			mapUI.drawNewMapObject(o);
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
		Log.d("UpdateObject","MapCont: "+o.getTitle());
		if(mapUI!=null){
			mapModel.updateObject(o);
			o.updateData(geocoder);
			mapUI.drawNewMapObject(o);
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
		
		if(areYouFind){
			if(olist.size() == 0)
				olist.add(you);
			mapUI.controller.animateTo(you.getPoint());
			mapUI.controller.setZoom(13);
			follow = true;
		}
		for(int i = 0; i < olist.size();i++){
			MapObject o = olist.get(i);
			mapModel.add(o);
			o.updateData(geocoder);
			mapUI.drawNewMapObject(o);
		}
	}

	public boolean animateTo(GeoPoint point){
		try{
			mapUI.controller.animateTo(point);
			return true;
		}catch(NullPointerException ne){
			return false;
		}
	}
	
	public void removeObject(MapObject o,boolean notify){
		Log.d("RemoveObject", "MapCont:"+o.getTitle());
		if(mapModel != null){
			mapModel.removeObject(o);
			mapUI.drawNewMapObject(o);
			if(notify){
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

				you = new You((GeoPoint)data, SessionController.getUser(), "Här är"+SessionController.getUser(),
						ResourceStatus.FREE, false);
				add(you,true);		// karta
			}
			else{
				you.setPoint((GeoPoint)data);
				updateObject(you,true);
			}

			if (mapUI != null){
				mapUI.drawNewMapObject(you);
				if(follow){
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
	
	public void sendTextMessage(String user){
		mapUI.sendTextMessage(user);
	}
	
	public void sendImageMessage(String user){
		mapUI.sendImageMessage(user);
	}
	
	public void callUser(String user){
		mapUI.callUser(user);
	}

	public void renewYou() {
		if (you!=null){
			you = new You(you.getPoint(), you.getTitle(), you.getSnippet(),
					you.getStatus(), you.isSOS());
			updateObject(you,true);		// karta
			if (mapUI != null){
				mapUI.drawNewMapObject(you);
				if(follow){
					mapUI.controller.animateTo(you.getPoint());
				}
			}
		}
	}
}
