package raddar.models;

import java.util.LinkedList;

/**
 * En klass som h�ller ihop alla 6-timmars block som yr.no delar
 * upp dagarna i.
 * @author andbo265
 *
 */
public class WeatherDay {
	/**
	 * Dagens datum (yyyy-MM-dd)
	 */
	public String date;
	/**
	 * En lista med WeatherBlocks. Varje WeatherBlock representerar v�dret
	 * under 6 timmar, varf�r en WeatherDay max b�r inneh�lla 4 WeatherBlocks
	 */
	public LinkedList<WeatherBlock> blocks = new LinkedList<WeatherBlock>();
	
	/**
	 * En ny WeatherDay med angivet datum
	 * @param date WeatherDay-objektets datum
	 */
	public WeatherDay(String date) {
		this.date = date;
	}
	
	/**
	 * Skapa ett nytt WeatherDay-objekt med exakt samma variabel-v�rden
	 * som day
	 * @param day WeatherDay-objektet vars v�rden vi kopierar
	 */
	public WeatherDay(WeatherDay day) {
		date = new String(day.date);
		blocks = new LinkedList<WeatherBlock>(day.blocks);
	}
}
