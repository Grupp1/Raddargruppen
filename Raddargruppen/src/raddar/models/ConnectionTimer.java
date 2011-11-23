package raddar.models;

import java.util.Observable;

public class ConnectionTimer extends Observable {
	
	
	
	/**
	 * Används ej för tillfället
	 * 
	 * @param updateTime Tiden i millisekunder som klienten ska bekräfta anslutningen
	 */
	
	/*
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
*/
}
