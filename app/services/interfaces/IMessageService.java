package services.interfaces;

import java.util.List;

import models.Message;
import models.view.ConversationBriefViewModel;
import models.view.ConversationDetailsViewModel;
import exceptions.AuthorizationException;
import exceptions.InvalidIdException;



public interface IMessageService {
	
	void send(Message message) throws AuthorizationException, InvalidIdException;

	ConversationDetailsViewModel getConversation(long currentUserId, long anotherUserId) throws AuthorizationException, InvalidIdException;

	List<ConversationBriefViewModel> getAllConversations(long userId);
	
	int getUnreadMessageCount(long userId);
	
}
