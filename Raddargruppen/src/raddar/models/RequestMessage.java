package raddar.models;

import raddar.controllers.SessionController;
import raddar.enums.MessageType;
import raddar.enums.RequestType;
/**
 * A request message sent to the server to request a certain 
 * service
 * @author danan612
 *
 */

public class RequestMessage extends Message {
	RequestType requestType;
	
	/**
	 * Create a request message with a certain requestType
	 * @param requestType The requestType of the current message to be set
	 */
	public RequestMessage(RequestType requestType){
		this.requestType = requestType;
		this.toUser = null;
		this.fromUser = SessionController.getUser();
		this.subject = null;
		this.data = null;
		this.type = MessageType.REQUEST;
	}
	/**
	 * Gets the requesttype for the current message
	 * @return The requestType
	 */
	public RequestType getRequestType(){
		return requestType;
	}
	
	
	
	@Override
	public String getFormattedMessage() {
		// TODO Auto-generated method stub
		return null;
	}

}
