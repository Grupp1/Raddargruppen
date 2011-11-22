package raddar.models;

import java.util.LinkedList;

public class WeatherDay {
	public String date;
	
	public LinkedList<WeatherBlock> blocks = new LinkedList<WeatherBlock>();
	
	public WeatherDay(String date) {
		this.date = date;
	}
	
	public WeatherDay(WeatherDay day) {
		date = new String(day.date);
		blocks = new LinkedList<WeatherBlock>(day.blocks);
	}
}
