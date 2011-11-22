package raddar.controllers;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

import raddar.enums.ResourceStatus;
import raddar.gruppen.R;
import raddar.models.ConnectionTimer;
import raddar.models.GPSModel;
import raddar.models.MapModel;
import raddar.models.MapObject;
import raddar.models.MapObjectList;
import raddar.models.You;
import raddar.views.MainView;
import raddar.views.MapUI;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;
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
	private ConnectionTimer timer;
	private int updateTime = 5000;

	/*
	 * Skickar vidare operationer i en ny tråd till MapModel 
	 */

	public MapCont(MainView m){
		gps  = new GPSModel(m, this);
		olist = MainView.db.getAllRowsAsArrays("map");
		//timer = new ConnectionTimer(this, updateTime);
	}

	public void declareMapUI(MapUI mapUI){
		this.mapUI = mapUI;
		geocoder = new Geocoder(mapUI.getBaseContext(), Locale.getDefault());
		if (!thread.isAlive()){
			run();
		}
	}

	public void add(MapObject o){
		MainView.db.addRow(o);
		mapModel.add(o);
	}

	public void updateObject(MapObject o){
		mapModel.updateObject(o);
	}

	public void run() {
		for(int i = 0; i < olist.size();i++){
			olist.get(i).updateData(new Geocoder(mapUI.getBaseContext(), Locale.getDefault()));
			mapModel = new MapModel(mapUI, MapCont.this);
			mapModel.add(olist.get(i));
		}
	}

	public void removeObject(MapObject mo){
		mapModel.removeObject(mo);
	}

	public void update(Observable o, Object data) {
		if (data instanceof GeoPoint){
			if (!areYouFind){
				areYouFind = true;
				you = new You((GeoPoint)data, "Din position", "Här är du", R.drawable.niklas, ResourceStatus.FREE);
				olist.add(you); // databas
				add(you);		// karta
				mapUI.controller.animateTo(you.getPoint());
				mapUI.controller.setZoom(13);
				mapUI.follow = true;
			}
			else{
				you.updateData(geocoder);
			}
			you.setPoint((GeoPoint)data);	

			if (mapUI.follow){
				mapUI.controller.animateTo(you.getPoint());
			}			
		}
		if (data instanceof MapObjectList){
			// Send information to server
			Log.d("HEEEJ", "HEEEJ");
			Gson gson = new Gson();
			for (int i=0; i<((MapObjectList) data).size(); i++){
				Log.d("For loopen", "i="+i);
				try {
					gson.toJson((MapObject) ((MapObjectList) data).getItem(i));
					new Sender((MapObject) ((MapObjectList) data).getItem(i),
							InetAddress.getByName(raddar.enums.ServerInfo.SERVER_IP),
							raddar.enums.ServerInfo.SERVER_PORT);
				} catch (UnknownHostException e) {
					e.printStackTrace();
					Log.d("Send MapObjects", "UnknownHostException");
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

}
