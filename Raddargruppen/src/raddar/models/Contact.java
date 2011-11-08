package raddar.models;

public class Contact {
	private String USER_NAME;
	private boolean isGroup;
	
	/**
	 * ADD A CONTACT IN THE CONTACT LIST
	 * 
	 * @param userName The contacts user name
	 * @param isGroup false if the contact is a user; true if the contact is a group of users.
	 */
	public Contact(String userName, boolean isGroup){
		this.USER_NAME = userName;
		this.isGroup = isGroup;
	}
	
	public String getUserName(){
		return USER_NAME;
	}
	
	public boolean isgroup(){
		return isGroup;
	}
}
