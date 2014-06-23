package services.interfaces;

import java.util.List;

import exceptions.AuthorizationException;
import models.Message;
import models.view.ConversationDetailsViewModel;
import models.view.ConversationBriefViewModel;
import models.view.MessageViewModel;



public interface IMessageService {
	
	void send(Message message) throws AuthorizationException;

	ConversationDetailsViewModel getConversation(long currentUserId, long anotherUserId) throws AuthorizationException;

	List<ConversationBriefViewModel> getAllConversations(long userId);
	
	int getUnreadMessageCount(long userId);
	
}
