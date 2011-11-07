package raddar.controllers;

import java.util.Observable;
import java.util.Observer;

import raddar.models.Fire;
import raddar.models.FireTruck;
import raddar.models.MapModel;
import raddar.models.MapObjectList;
import raddar.models.Resource;
import raddar.views.MapUI;
import android.graphics.drawable.Drawable;

public class MapCont implements Observer, Runnable{

	private MapModel mapModel;
	private MapObjectList fireList;
	private MapObjectList resourceList;
	private Drawable d;
	private Thread thread = new Thread(this);
	private Object o;

	/*
	 * Kontrollerar vilken typ av objekt som lagts till p� kartan. 
	 */

	public MapCont(final MapUI mapUI, Object o){
		mapModel = new MapModel(mapUI, this);
		this.o = o;
		thread.start();
	}

	public void add(Object o){

		if (o instanceof Fire){
			Fire f = (Fire) o;
			mapModel.add(f);

		}
		if(o instanceof FireTruck){

			FireTruck r = (FireTruck) o;

			mapModel.add(r);

		}

	}

	public void run() {
		add(o);

	}
	
	public void update(Observable o, Object data) {


	}


}
