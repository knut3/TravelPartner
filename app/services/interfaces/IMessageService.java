package services.interfaces;

import java.util.List;

import exceptions.AuthorizationException;
import models.Message;
import models.view.MessageNotification;



public interface IMessageService {
	
	void send(Message message) throws AuthorizationException;

	List<Message> getConversation(long userId, long anotherUserId);

	List<MessageNotification> getAllConversations(long userId);
	
}
