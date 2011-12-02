package raddar.models;

import raddar.enums.MessageType;
import raddar.enums.OnlineOperation;

public class OnlineUsersMessage extends Message  {

	private OnlineOperation onlineOperation;
	private String userName;
	
	public void OnlineUserMessage(OnlineOperation onlineOperation, String userName){
		this.onlineOperation = onlineOperation;
		this.userName = userName;
		this.type = MessageType.ONLINE_USERS;
	}
	
	@Override
	public String getFormattedMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	public OnlineOperation getOnlineOperation(){
		return onlineOperation;
	}
	
	public String getUserName(){
		return userName;
	}
}
