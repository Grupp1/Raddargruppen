package raddar.models;

import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;

import raddar.controllers.MapCont;

public class ConnectionTimer extends Observable {
	
	/**
	 * 
	 * @param updateTime Tiden i millisekunder som klienten ska bekräfta anslutningen
	 */
	public ConnectionTimer(MapCont mapCont, int updateTime){
		addObserver(mapCont);
		Timer timer = new Timer();
		timer.schedule(send(), updateTime);
	}
	
	public TimerTask send(){
		setChanged();
		notifyObservers("updater");
		return null;
	}

}
