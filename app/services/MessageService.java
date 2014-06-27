package services;

import java.util.ArrayList;
import java.util.List;

import play.libs.Json;

import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.FetchConfig;
import com.avaje.ebean.Query;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import com.google.inject.Inject;

import exceptions.AuthenticationException;
import exceptions.AuthorizationException;
import models.Message;
import models.User;
import models.view.ConversationDetailsViewModel;
import models.view.ConversationBriefViewModel;
import models.view.MessageNotificationViewModel;
import models.view.MessageViewModel;
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
							Expr.eq("sender.id", message.sender.id), 
							Expr.eq("recipient.id", message.recipient.id)
						),
						Expr.and(
							Expr.eq("sender.id", message.recipient.id), 
							Expr.eq("recipient.id", message.sender.id)
						)
					)
					.findRowCount() > 0;
		
		boolean withinRange = userService.areUsersWithinRange(message.recipient.id, message.sender.id);
		
		if(!hasConversation && !withinRange)
			throw new AuthorizationException("You are not allowed to write to this user");
		
		message.save();
		User sender = User.find.byId(message.sender.id);
		MessageNotificationViewModel messageNotification = new MessageNotificationViewModel();
		messageNotification.userName = sender.firstName;
		messageNotification.userId = sender.id;
		messageNotification.userPictureId = sender.profilePicture.id;
		messageNotification.message = MessageViewModel.fromMessage(message, message.recipient.id);
		eventSourceService.sendEvent(message.recipient.id, Json.toJson(messageNotification), EventNames.NEW_MESSAGE);		
	}

	@Override
	public ConversationDetailsViewModel getConversation(long currentUserId, long anotherUserId) throws AuthorizationException {
		List<Message> messages = Message.find
			.where()
			.or(
				Expr.and(Expr.eq("sender.id", currentUserId), Expr.eq("recipient.id", anotherUserId)),
				Expr.and(Expr.eq("sender.id", anotherUserId), Expr.eq("recipient.id", currentUserId))
				)
			.findList();
		
		if(messages.isEmpty()){
			boolean withinRange = userService.areUsersWithinRange(currentUserId, anotherUserId);
			if(!withinRange)
				throw new AuthorizationException("You can not have a conversation with this user");
		}
			
		
		ConversationDetailsViewModel result = new ConversationDetailsViewModel();
		User anotherUser = User.find.byId(anotherUserId);
		result.userId = anotherUserId;
		result.userName = anotherUser.firstName;
		result.userPictureId = anotherUser.profilePicture.id;
		for (Message message : messages) {
			result.messages.add(MessageViewModel.fromMessage(message, currentUserId));
			if(message.sender.id != currentUserId && !message.isRead){
				message.isRead = true;
				message.save();
				result.unreadMessageCount++;
			}
		}
		
		return result;
	}

	@Override
	public List<ConversationBriefViewModel> getAllConversations(long currentUserId) {
		
		List<ConversationBriefViewModel> result = new ArrayList<ConversationBriefViewModel>();
		
		String sql =   "select ID, SENDER_ID, RECIPIENT_ID, MESSAGE from MESSAGE X"
				  	+ " where ID >= all"
				    + " (select ID FROM MESSAGE Y"
				    + " where Y.SENDER_ID = X.SENDER_ID"
				    + " and Y.RECIPIENT_ID = X.RECIPIENT_ID)";
		
		 RawSql rawSql = 
				  RawSqlBuilder.parse(sql)
				  .columnMapping("ID", "id")
				  .columnMapping("SENDER_ID", "sender.id")
			      .columnMapping("RECIPIENT_ID", "recipient.id")
			      .columnMapping("MESSAGE", "message")
				      .create();
		
		List<Message> receivedMessages = Message.find
				.setRawSql(rawSql)
				.fetch("sender", new FetchConfig().query())
				.fetch("recipient", new FetchConfig().query())
				.where().or(
						Expr.eq("sender.id", currentUserId),
						Expr.eq("recipient.id", currentUserId)
						)
				.orderBy("id desc")
				.findList();
		
		List<Long> uniqueUsers = new ArrayList<Long>();
		for (Message message : receivedMessages) {
			User otherUser;

			if(message.sender.id != currentUserId)
				otherUser = message.sender;
			else
				otherUser = message.recipient;
				
			if(!uniqueUsers.contains(otherUser.id)){
				ConversationBriefViewModel c = new ConversationBriefViewModel();
				c.userId = otherUser.id;
				c.userName = otherUser.firstName;
				c.userPictureId = otherUser.profilePicture.id;
				c.latestMessage = MessageViewModel.fromMessage(message, currentUserId);
				result.add(c);
				uniqueUsers.add(otherUser.id);
			}
		}
		
		return result;
		
		
	}

	@Override
	public int getUnreadMessageCount(long userId) {
		
		return Message.find.where()
			.eq("recipient.id", userId)
			.eq("isRead", false)
			.findRowCount();
		
	}

	
}
