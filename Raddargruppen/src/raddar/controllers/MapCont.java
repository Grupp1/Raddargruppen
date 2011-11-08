package raddar.controllers;

import java.util.Observable;
import java.util.Observer;

import raddar.models.MapModel;
import raddar.models.MapObject;
import raddar.models.MapObjectList;
import raddar.views.MapUI;
import android.graphics.drawable.Drawable;

public class MapCont implements Observer, Runnable{

	private MapModel mapModel;
	private Thread thread = new Thread(this);
	private MapObject o;

	/*
	 * Kontrollerar vilken typ av objekt som lagts till på kartan. 
	 */

	public MapCont(final MapUI mapUI, MapObject o){
		mapModel = new MapModel(mapUI, this);
		this.o = o;
		thread.start();
	}

	public void add(MapObject o){
		mapModel.add(o);
	}

	public void run() {
		add(o);
	}
	
	public void update(Observable o, Object data) {


	}


}
