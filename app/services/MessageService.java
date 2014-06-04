package services;

import java.util.List;

import play.libs.Json;

import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionList;
import com.google.inject.Inject;

import exceptions.AuthorizationException;
import models.Message;
import models.User;
import models.view.MessageNotification;
import services.interfaces.IEventSourceService;
import services.interfaces.IMessageService;
import services.interfaces.IUserService;
import utils.EventNames;

public class MessageService implements IMessageService{

	@Inject
	IUserService userService;
	@Inject
	IEventSourceService eventSourceService;
	
	@Override
	public void send(Message message) throws AuthorizationException{
		
		boolean hasConversation = 
				Message.find.where()
					.or(
						Expr.and(
							Expr.eq("senderId", message.senderId), 
							Expr.eq("recipientId", message.recipientId)
						),
						Expr.and(
							Expr.eq("senderId", message.recipientId), 
							Expr.eq("recipientId", message.senderId)
						)
					)
					.findRowCount() > 0;
		
		boolean withinRange = userService.areUsersWithinRange(message.recipientId, message.senderId);
		
		if(!hasConversation && !withinRange)
			throw new AuthorizationException();
		
		message.save();
		MessageNotification messageNotification = MessageNotification.fromMessage(message);
		messageNotification.senderName = User.find.byId(message.senderId).firstName;
		eventSourceService.sendEvent(message.recipientId, Json.toJson(messageNotification), EventNames.NEW_MESSAGE);		
	}

	@Override
	public List<Message> getConversation(long userId, long anotherUserId) {
		List<Message> messages = Message.find.where()
			.or(
				Expr.and(Expr.eq("senderId", userId), Expr.eq("recipientId", anotherUserId)),
				Expr.and(Expr.eq("senderId", anotherUserId), Expr.eq("recipientId", userId))
				).findList();
		
		return messages;
	}

	@Override
	public List<MessageNotification> getAllConversations(long currentUserId) {
		
		return null;
		
	}

	
}
