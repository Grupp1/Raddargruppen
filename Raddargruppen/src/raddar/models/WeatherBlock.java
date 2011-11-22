package raddar.models;

public class WeatherBlock {

	public String from;
	public String to;
	public String speed;
	public String direction;
	public String temp;
	public String weather;
	
	public WeatherBlock() {
		
	}
	
	public WeatherBlock(WeatherBlock w) {
		from = new String(w.from);
		to = new String(w.to);
		speed = new String(w.speed);
		direction = new String(w.direction);
		temp = new String(w.temp);
	}
}
