package raddar.models;

import java.util.Observable;

public class ConnectionTimer extends Observable {
	
	
	
	/**
	 * Anv�nds ej f�r tillf�llet
	 * 
	 * @param updateTime Tiden i millisekunder som klienten ska bekr�fta anslutningen
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
