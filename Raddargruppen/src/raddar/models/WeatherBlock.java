package raddar.models;

public class WeatherBlock {

	/**
	 * Klockslaget då väderinformationen börjar gälla
	 */
	public String from;
	/**
	 * Klockslaget då väderinformationen upphör att gälla
	 */
	public String to;
	/**
	 * Vindens fart
	 */
	public String speed;
	/**
	 * Vindens riktning, där t ex North (eller N) innebär att vinden kommer FRÅN norr
	 */
	public String direction;
	/**
	 * Temperaturen angiven i Celsius
	 */
	public String temp;
	/**
	 * Vädertyp, t ex "partly cloudy"
	 */
	public String weather;
	
	/**
	 * Ett nytt WeatherBlock objekt med tomma variabler
	 */
	public WeatherBlock() {
		
	}
	
	/**
	 * Kopiera ett WeatherBlock-objekt
	 * @param w WeatherBlock-objektet vars variabelvärden ska kopieras
	 */
	public WeatherBlock(WeatherBlock w) {
		from = new String(w.from);
		to = new String(w.to);
		speed = new String(w.speed);
		direction = new String(w.direction);
		temp = new String(w.temp);
	}
}
