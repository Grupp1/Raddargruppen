package raddar.models;

public class WeatherBlock {

	/**
	 * Klockslaget d� v�derinformationen b�rjar g�lla
	 */
	public String from;
	/**
	 * Klockslaget d� v�derinformationen upph�r att g�lla
	 */
	public String to;
	/**
	 * Vindens fart
	 */
	public String speed;
	/**
	 * Vindens riktning, d�r t ex North (eller N) inneb�r att vinden kommer FR�N norr
	 */
	public String direction;
	/**
	 * Temperaturen angiven i Celsius
	 */
	public String temp;
	/**
	 * V�dertyp, t ex "partly cloudy"
	 */
	public String weather;
	
	/**
	 * Ett nytt WeatherBlock objekt med tomma variabler
	 */
	public WeatherBlock() {
		
	}
	
	/**
	 * Kopiera ett WeatherBlock-objekt
	 * @param w WeatherBlock-objektet vars variabelv�rden ska kopieras
	 */
	public WeatherBlock(WeatherBlock w) {
		from = new String(w.from);
		to = new String(w.to);
		speed = new String(w.speed);
		direction = new String(w.direction);
		temp = new String(w.temp);
	}
}
