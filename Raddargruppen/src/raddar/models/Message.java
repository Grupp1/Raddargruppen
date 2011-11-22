package raddar.models;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import raddar.enums.MessagePriority;
import raddar.enums.MessageType;

/* Exempel meddelande */
/*
 * 
 * Priority: normal
 * Content-Type: text/plain
 * From-User: Borche
 * To-User: Simon
 * 
 * Hejsan, vi har en situation här där en väg är blockerad av bla
 * bla bla bla bla bla bla ahejasdkansdjkanskdjan asdkjnwquwdbasd
 * qjwhebajsdhb bla bla bla asduhquiwdhqd ajdhbqasd  ahejasd qd qw
 * qwekqjwejkqdhashdy  qweyuasd qyu. Hejasd qwuiasdjkqkwd, asdhguqy
 * asdhybquwydbajsdbhqhjwbdjqhwbdjqsdi8 qwduqiusd Hejasd uq kjsj. SG
 * ahsdbjqhbd j.
 * 
 * MVh
 * 
 * Borche
 */

/*
 * SUPERKLASS TILL ALLA ANDRA MEDDELANDEN
 */
public abstract class Message {
	
	// Carriage-return och Line-feed
	public static final String CRLF = "\r\n";
		
	// Header attributer
	public static final String HEADER_TYPE = "Content-Type: ";
	public static final String HEADER_PRIO = "Priority: ";
	public static final String HEADER_FROM = "From-User: ";
	public static final String HEADER_TO = "To-User: ";
	public static final String HEADER_DATE = "Date: ";
	public static final String HEADER_SUBJECT = "Subject: ";
	
	/* Värden på attributerna ovan */
	
	// Content-Type värden
	public static final String TYPE_TEXT = "text/plain";
	public static final String TYPE_JPEG = "image/jpeg";
			
	// Typ av message, sändare och mottagare
	protected MessageType type;
	protected String fromUser;
	protected String toUser;
	// Ämnesrad
	protected String subject;
	// Meddelandets datum. Default är då meddelandet skapades.
	protected String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
	// Meddelandets data
	//Temporärt en string bara för att testa
	protected String data;
	
	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject.trim();
	}
	
	//Här också
	public String getData() {
		return data;
	}
	
	public void setData(String data) {
		this.data = data;
	}
	
	public void setType(MessageType type) {
		this.type = type;
	}
	
	public MessageType getType() {
		return type;
	}
	
	public void setSrcUser(String srcUser) {
		this.fromUser = srcUser.trim();
	}
	
	public String getSrcUser() {
		return fromUser.trim();
	}
	
	public void setDestUser(String destUser) {
		this.toUser = destUser.trim();
	}
	
	public String getDestUser() {
		return toUser;
	}
	
	public void setDate(String date) {
		this.date = date;
	}
	
	public String getDate() {
		return date;
	}
	
	public void setMessage(String message) {
		this.data = message;
	}
	
	public void prepend(String message) {
		this.data = message + data;
	}
	
	public void append(String message) {
		this.data += message;
	}
	
	public abstract String getFormattedMessage();
}

