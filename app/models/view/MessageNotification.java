package models.view;

import models.Message;


public class MessageNotification{

	public long id;
	public long senderId;
	public String senderName;
	public String message;
	
	public static MessageNotification fromMessage(Message m){
		MessageNotification mn = new MessageNotification();
		mn.id = m.id;
		mn.senderId = m.senderId;
		mn.message = m.message;
		return mn;
	}
	
}
