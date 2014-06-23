package models.view;

import utils.RelativeTime;
import models.Message;


public class MessageViewModel{

	public long id;
	public String message;
	public boolean sentByMe;
	public String dateTimeSent;
	public boolean isRead;
	
	public static MessageViewModel fromMessage(Message m, long currentUserId){
		MessageViewModel mvm = new MessageViewModel();
		mvm.id = m.id;
		mvm.message = m.message;
		mvm.dateTimeSent = RelativeTime.fromNow(m.dateTimeSent.toLocalDateTime());
		
		if(currentUserId == m.sender.id){
			mvm.sentByMe = true;
			mvm.isRead = true;
		}

		else{
			mvm.sentByMe = false;
			mvm.isRead = m.isRead;
		}
		
		return mvm;
	}
	
}
