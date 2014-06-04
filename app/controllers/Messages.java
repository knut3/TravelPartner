package controllers;

import models.Message;
import play.mvc.Http.RequestBody;
import play.mvc.Result;
import services.interfaces.IMessageService;
import annotations.Authorization.Authorized;

import com.google.inject.Inject;

import exceptions.AuthorizationException;

@Authorized
public class Messages extends BaseController {
	
	@Inject
	private IMessageService messageService;
	
	public Result send(long recipientId) throws AuthorizationException{	
		RequestBody body = request().body();
		String msg = body.asText();
		messageService.send(new Message(this.getCurrentUserId(), recipientId, msg));
		return ok();
	}
	
	public Result getConversation(long userId){		
		
		messageService.getConversation(this.getCurrentUserId(), userId);
		return ok();
	}
	
	public Result getAllConversations(){		
		
		messageService.getAllConversations(this.getCurrentUserId());
		return ok();
	}
	
	
}
