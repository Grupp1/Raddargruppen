package tddd36.server;

import java.text.DateFormat;
import java.text.ParseException;
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
 * Hejsan, vi har en situation h�r d�r en v�g �r blockerad av bla
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
	
	/* V�rden p� attributerna ovan */
	
	// Content-Type v�rden
	public static final String TYPE_TEXT = "text/plain";
	public static final String TYPE_JPEG = "image/jpeg";
			
	// Typ av message, s�ndare och mottagare
	protected MessageType type;
	protected MessagePriority priority;
	protected String fromUser;
	protected String toUser;
	// �mnesrad
	protected String subject;
	// Meddelandets datum. Default �r d� meddelandet skapades.
	protected Date date = new Date();
	// Meddelandets data
	protected String data;
	
	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public void setPriority(MessagePriority priority) {
		if (priority == MessagePriority.NORMAL || priority == MessagePriority.HIGH)
			this.priority = priority;
		else
			this.priority = MessagePriority.NORMAL;
	}
	
	public MessagePriority getPriority() {
		return priority;
	}
	
	public void setType(MessageType type) {
		this.type = type;
	}
	
	public MessageType getType() {
		return type;
	}
	
	public void setSrcUser(String srcUser) {
		this.fromUser = srcUser;
	}
	
	public String getSrcUser() {
		return fromUser;
	}
	
	public void setDestUser(String destUser) {
		this.toUser = destUser;
	}
	
	public String getDestUser() {
		return toUser;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}
	
	public void setDate(String date) {
		DateFormat df = DateFormat.getDateTimeInstance();
		try {
			this.date = df.parse(date);
		} catch (ParseException e) { 
			e.printStackTrace();
		}
	}
	
	public Date getDate() {
		return date;
	}
	
	public String getFormattedDate() {
		DateFormat df = DateFormat.getDateTimeInstance();
		return df.format(date);
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
	
	public String getMessage() {
		return data;
	}
	
	public abstract String getFormattedMessage();
}
