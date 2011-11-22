package raddar.models;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

public class XMLFetcher extends DefaultHandler {

	private final static String WEATHER_URL = "http://www.yr.no/place/Sweden/%C3%96sterg%C3%B6tland/Link%C3%B6ping/varsel.xml";

	// private final static String TRAFFIC_URL = "";

	private WeatherDay wd = new WeatherDay(
			new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
	private WeatherBlock wb;

	public LinkedList<WeatherDay> list = new LinkedList<WeatherDay>();
	public String sunrise = "";
	public String sunset = "";

	public XMLFetcher() {
		parseDocument();
	}

	public void parseDocument() {
		SAXParserFactory spf = SAXParserFactory.newInstance();
		spf.setValidating(false);
		try {
			SAXParser sp = spf.newSAXParser();
			URL url = new URL(WEATHER_URL);
			URLConnection con = url.openConnection();

			sp.parse(new InputSource(con.getInputStream()), this);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void startDocument() throws SAXException {

	}

	@Override
	public void endDocument() throws SAXException {

	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if (qName.equals("sun")) {
			sunrise = attributes.getValue("rise").split("T")[1].substring(0,5);
			sunset = attributes.getValue("set").split("T")[1].substring(0, 5);
		}
		if (qName.equals("time")) {
			wb = new WeatherBlock();
			String[] parts = attributes.getValue("from").split("T");
			String date = parts[0];
			String from = parts[1];
			from = from.substring(0, 5);
			if (!date.equals(wd.date)) {
				list.add(wd);
				wd = new WeatherDay(date);
			}
			wb.from = from;
			parts = attributes.getValue("to").split("T");
			String to = parts[1];
			to = to.substring(0, 5);
			wb.to = to;
		}
		if (qName.equals("symbol")) {
			String weather_type = attributes.getValue("name");
			wb.weather = weather_type;
		}
		if (qName.equals("windDirection")) {
			String direction = attributes.getValue("code");
			wb.direction = direction;
		}
		if (qName.equals("windSpeed")) {
			String speed = attributes.getValue("mps");
			wb.speed = speed + " mps";
		}
		if (qName.equals("temperature")) {
			String value = attributes.getValue("value");
			wb.temp = value + "˚C";
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (qName.equals("time")) {
			wd.blocks.add(new WeatherBlock(wb));
			wb = new WeatherBlock();
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) {

	}

	@Override
	public void error(SAXParseException e) {
		Log.d("XML", "ERROR: " + e.getMessage());
	}

	@Override
	public void warning(SAXParseException e) {
		Log.d("XML", "WARNING: " + e.getMessage());
	}

}
