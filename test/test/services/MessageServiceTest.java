package test.services;

import static org.fest.assertions.Assertions.assertThat;

import java.util.List;
import java.util.UUID;

import models.Location;
import models.Message;
import models.Picture;
import models.User;
import models.view.ConversationBriefViewModel;
import models.view.ConversationDetailsViewModel;

import org.junit.Before;
import org.junit.Test;

import services.EventSourceService;
import services.ImageService;
import services.MessageService;
import services.UserService;
import services.interfaces.IMessageService;
import config.BaseTest;
import exceptions.AuthorizationException;
import exceptions.InvalidIdException;

public class MessageServiceTest extends BaseTest{

	
	private static final int NUM_USERS = 5;
	private IMessageService messageService = new MessageService(new UserService(new ImageService()), new EventSourceService());
	
	@Before
	public void insertUsers() {		
		for(long i = 1; i <= NUM_USERS; i++){
			User user = new User(i, "User"+i, i%2==0?"male":"female");
			Location userLoc = new Location(user.id, 10f + i*(UserService.RADIUS/3), 10f, "");
			userLoc.save();
			user.setLocation(userLoc);
			Picture profilePic = new Picture(UUID.randomUUID().toString(), i, 10, 10);
			profilePic.save();
			user.profilePicture = profilePic;
			user.save();
		}
	}
	
	@Test
	public void send_DatabaseState_UsersNearby() throws AuthorizationException, InvalidIdException{
		Message msg = new Message(1, 2, "msg");
		messageService.send(msg);		
		List<Message> msgs = Message.find.all();				
		assertThat(msgs.size()).isEqualTo(1);
		assertThat(msgs.get(0).message).isEqualTo("msg");
		assertThat(msgs.get(0).recipient.id).isEqualTo(2);
		assertThat(msgs.get(0).sender.id).isEqualTo(1);
	}
	
	@Test
	public void send_DatabaseState_UsersHaveExchangedMsgs() throws AuthorizationException, InvalidIdException{
		new Message(1, NUM_USERS, "existing message").save();
		
		Message msg = new Message(1, NUM_USERS, "new msg");
		messageService.send(msg);		
		List<Message> msgs = Message.find.all();				
		assertThat(msgs.size()).isEqualTo(2);
	}
	
	@Test
	public void send_DatabaseState_UsersHaveExchangedMsgs_2() throws AuthorizationException, InvalidIdException{
		new Message(NUM_USERS, 1, "existing message").save();
		
		Message msg = new Message(1, NUM_USERS, "new msg");
		messageService.send(msg);		
		List<Message> msgs = Message.find.all();				
		assertThat(msgs.size()).isEqualTo(2);
	}
	
	@Test(expected = InvalidIdException.class)
	public void send_InvalidUserId() throws AuthorizationException, InvalidIdException{
		Message msg = new Message(1, 666, "msg");
		messageService.send(msg);
	}
	
	@Test(expected = AuthorizationException.class)
	public void send_TooFarAwayAndNoMessageExchange() throws AuthorizationException, InvalidIdException{
		Message msg = new Message(1, NUM_USERS, "msg");
		messageService.send(msg);
	}
	
	@Test
	public void getConversation() throws AuthorizationException, InvalidIdException{
		new Message(1, NUM_USERS, "msg").save();
		new Message(NUM_USERS, 1, "msg").save();
		new Message(1, NUM_USERS, "msg").save();
		
		ConversationDetailsViewModel conversation = messageService.getConversation(1, NUM_USERS);
		assertThat(conversation.userId).isEqualTo(NUM_USERS);
		assertThat(conversation.unreadMessageCount).isEqualTo(1);
		assertThat(conversation.messages.size()).isEqualTo(3);
	}
	
	@Test
	public void getConversation_ReceivedOnly() throws AuthorizationException, InvalidIdException{
		new Message(NUM_USERS, 1, "msg").save();
		
		ConversationDetailsViewModel conversation = messageService.getConversation(1, NUM_USERS);
		assertThat(conversation.unreadMessageCount).isEqualTo(1);
		assertThat(conversation.messages.size()).isEqualTo(1);
	}
	
	@Test
	public void getConversation_SentOnly() throws AuthorizationException, InvalidIdException{
		new Message(1, NUM_USERS, "msg").save();
		
		ConversationDetailsViewModel conversation = messageService.getConversation(1, NUM_USERS);
		assertThat(conversation.unreadMessageCount).isEqualTo(0);
		assertThat(conversation.messages.size()).isEqualTo(1);
	}
	
	@Test
	public void getConversation_ReadMessage() throws AuthorizationException, InvalidIdException{
		Message msg = new Message(NUM_USERS, 1, "msg");
		msg.isRead = true;
		msg.save();
		
		ConversationDetailsViewModel conversation = messageService.getConversation(1, NUM_USERS);
		assertThat(conversation.unreadMessageCount).isEqualTo(0);
	}
	
	@Test(expected = InvalidIdException.class)
	public void getConversation_InvalidId() throws AuthorizationException, InvalidIdException{
		messageService.getConversation(1, 666);
	}
	
	@Test(expected = AuthorizationException.class)
	public void getConversation_NotNearbyAndNoMessageExchange() throws AuthorizationException, InvalidIdException{
		messageService.getConversation(1, NUM_USERS);
	}
	
	@Test
	public void getConversation_NearbyAndNoMessageExchange() throws AuthorizationException, InvalidIdException{
		ConversationDetailsViewModel conversation = messageService.getConversation(1, 2);
		assertThat(conversation.messages.size()).isEqualTo(0);
	}
	
	@Test
	public void getAllConversations(){
		new Message(1, 2, "correct").save();
		new Message(NUM_USERS, 1, "correct").save();
		new Message(NUM_USERS, 2, "incorrect").save();
		
		List<ConversationBriefViewModel> conversations = messageService.getAllConversations(1);
		
		assertThat(conversations.size()).isEqualTo(2);
		
		for(ConversationBriefViewModel c : conversations){
			assertThat(c.latestMessage.message).isEqualTo("correct");
		}
	}
}
