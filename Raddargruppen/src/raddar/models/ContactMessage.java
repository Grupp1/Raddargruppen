package raddar.models;

import raddar.controllers.SessionController;
import raddar.enums.MessageType;

public class ContactMessage extends Message {
	private String contact;
	
	public ContactMessage(String contact){
		this.contact = contact;
		this.toUser = null;
		this.fromUser = SessionController.getUser();
		this.subject = null;
		this.data = null;
		this.type = MessageType.CONTACT;
	}
	public String getContact(){
		return contact;
	}
	public Contact toContact(){
		return new Contact(contact, false);
	}
	
	@Override
	public String getFormattedMessage() {
		return null;
	}
	
}  
