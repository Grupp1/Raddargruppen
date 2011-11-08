package raddar.models;

public class Contact {
	
	private String userName;
	private boolean isGroup;
	
	/**
	 * ADD A CONTACT IN THE CONTACT LIST
	 * 
	 * @param userName The contacts user name
	 * @param isGroup false if the contact is a user; true if the contact is a group of users.
	 */
	public Contact(String userName, boolean isGroup){
		this.userName = userName;
		this.isGroup = isGroup;
	}
	
	public String getUserName(){
		return userName;
	}
	
	public boolean isgroup(){
		return isGroup;
	}
}
