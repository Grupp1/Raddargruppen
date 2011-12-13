package raddar.models;

public class Contact {
	
	private String userName;
	private boolean isGroup;
	private String sipUsr;
	private String sipPw;
	
	/**
	 * ADD A CONTACT IN THE CONTACT LIST
	 * 
	 * @param userName The contacts user name
	 * @param isGroup false if the contact is a user; true if the contact is a group of users.
	 */
	public Contact(String userName, boolean isGroup, String sipUsr, String sipPw){
		this.userName = userName;
		this.isGroup = isGroup;
		this.sipUsr = sipUsr;
		this.sipPw = sipPw;
	}
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
	
	public String getSipUsr(){
		return sipUsr;
	}
	
	public String getSipPw(){
		return sipPw;
	}
	
	public void setSipUsr(String sipUsr){
		this.sipUsr = sipUsr;
	}
	
	public void setSipPw(String sipPw){
		this.sipPw = sipPw;
	}
	@Override
	public boolean equals(Object o) {
		if(o instanceof Contact)
			return ((Contact) o).getUserName().equals(userName);
		return false;
	}
}
