package raddar.models;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

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
	
	private int count = 0;

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
		Log.d("XML", "COUNT: " + count);
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if (qName.equals("time")) {
			String[] parts = attributes.getValue("from").split("T");
			Log.d("XML", "FRÅN: ");
			Log.d("XML", "Datum: " + parts[0]);
			Log.d("XML", "Tid: " + parts[1]);
			parts = attributes.getValue("to").split("T");
			Log.d("XML", "TILL: ");
			Log.d("XML", "Datum: " + parts[0]);
			Log.d("XML", "Tid: " + parts[1]);
			count++;
		}
		if (qName.equals("symbol")) {
			String weather_type = attributes.getValue("name");
			Log.d("XML", "Väderlek: " + weather_type);
		}
		if (qName.equals("windDirection")) {
			// String degrees = attributes.getValue("deg");
			String direction = attributes.getValue("name");
			Log.d("XML", "VIND: ");
			Log.d("XML", "Riktning: " + direction);
		} 
		if (qName.equals("windSpeed")) {
			String speed = attributes.getValue("mps");
			String wind_type = attributes.getValue("name");
			Log.d("XML", "Fart: " + speed);
			Log.d("XML", "Typ: " + wind_type);
		}
		if (qName.equals("temperature")) {
			String unit = attributes.getValue("unit");
			String value = attributes.getValue("value");
			Log.d("XML", "Temperatur: " + value + " " + unit);
			Log.d("XML", "##---------------------##: ");
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
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
	
	private String getTodaysDate() {
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(d);
	}
	
}
