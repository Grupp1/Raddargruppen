package tddd36.server;

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
public abstract class Message {
	
	// Carriage-return och Line-feed
	public static final String CRLF = "\r\n";
		
	// Header attributer
	public static final String HEADER_TYPE = "Content-Type: ";
	public static final String HEADER_PRIO = "Priority: ";
	public static final String HEADER_FROM = "From-User: ";
	public static final String HEADER_TO = "To-User: ";	
	
	/* Värden på attributerna ovan */
	
	// Content-Type värden
	public static final String TYPE_TEXT = "text/plain";
	public static final String TYPE_JPEG = "image/jpeg";
			
	// Typ av message, sändare och mottagare
	protected MessageType type;
	protected MessagePriority priority;
	protected String srcUser;
	protected String destUser;
	
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
		this.srcUser = srcUser;
	}
	
	public String getSrcUser() {
		return srcUser;
	}
	
	public void setDestUser(String destUser) {
		this.destUser = destUser;
	}
	
	public String getDestUser() {
		return destUser;
	}
	
	public abstract String getFormattedMessage();
}

