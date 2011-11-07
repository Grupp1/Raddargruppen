package raddar.controllers;

import java.util.Observable;
import java.util.Observer;

import raddar.models.Fire;
import raddar.models.MapModel;
import raddar.models.MapObjectList;
import raddar.models.Resource;
import raddar.views.MapUI;
import android.graphics.drawable.Drawable;

public class MapCont implements Observer {

	private MapModel mapModel;
	private MapObjectList fireList;
	private MapObjectList resourceList;
	private Drawable d;



	/*
	 * Kontrollerar vilken typ av objekt som lagts till på kartan. 
	 */
	
	//d = new Drawable(fire);
	//fireList = new MapObjectList(d);

	public void add(Object o){

		if (o instanceof Fire){
			Fire f = (Fire) o;
			mapModel.addFire(f);

		}
		if(o instanceof Resource){
			Resource r = (Resource) o;
			mapModel.addResource(r);

		}

	}

	public void update(Observable o, Object data) {


	}


}
