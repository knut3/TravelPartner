package controllers;

import java.util.List;

import models.Message;
import models.view.ConversationBriefViewModel;
import models.view.ConversationDetailsViewModel;
import play.libs.Json;
import play.mvc.Http.RequestBody;
import play.mvc.Result;
import services.interfaces.IMessageService;
import annotations.Authentication.RequiresAuthentication;

import com.google.inject.Inject;

import exceptions.AuthorizationException;
import exceptions.InvalidIdException;

@RequiresAuthentication
public class Messages extends BaseController {
	
	@Inject
	private IMessageService messageService;
	
	public Result send(long recipientId) throws AuthorizationException, InvalidIdException{	
		RequestBody body = request().body();
		String msg = body.asText();
		messageService.send(new Message(this.getCurrentUserId(), recipientId, msg));
		return ok();
	}
	
	public Result getConversation(long userId) throws AuthorizationException, InvalidIdException{		
		
		ConversationDetailsViewModel msgs = messageService.getConversation(this.getCurrentUserId(), userId);
		return ok(Json.toJson(msgs));
	}
	
	public Result getAllConversations(){		
		
		List<ConversationBriefViewModel> messages = messageService.getAllConversations(this.getCurrentUserId());
		return ok(Json.toJson(messages));
	}
	
	public Result getUnreadMessageCount(){
		int unreadMsgs = messageService.getUnreadMessageCount(this.getCurrentUserId());
		return ok(String.valueOf(unreadMsgs));
	}
	
	
}
