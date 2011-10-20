package raddar.models;

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
		
	// Header attributer
	public static final String HEADER_PRIO = "Priority: ";
	public static final String HEADER_TYPE = "Content-Type: ";
	public static final String HEADER_FROM = "From-User: ";
	public static final String HEADER_TO = "To-User: ";	
	
	/* Värden på attributerna ovan */
	
	// Content-Type värden
	public static final String TYPE_TEXT = "text/plain";
	public static final String TYPE_JPEG = "image/jpeg";
		
	// Prioritetsvärden
	//public static final int PRIO_NORMAL = 0;
	//public static final int PRIO_HIGH = 1;
	
	// Typ av message, sändare och mottagare
	protected MessagePriority priority;
	protected MessageType type;
	protected String fromUser;
	protected String toUser;
	
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

}

