package controllers;

import java.util.List;

import models.User;
import play.libs.Json;
import play.mvc.Result;
import services.interfaces.IUserService;
import annotations.Authorization.Authorized;

import com.google.inject.Inject;

import exceptions.NoLocationException;

@Authorized
public class Users extends BaseController {
	
	@Inject
	IUserService userService;
	
	public Result getUser(Long id){
		
		User user = User.find.byId(id);
		if(user == null)
			return badRequest("Invalid ID");
		return ok(Json.toJson(user));
		
	}
	
	public Result getUsersNearYou() throws NoLocationException{
		
		List<User> users = userService.getUsersNearby(getCurrentUserId());
		
		return ok(Json.toJson(users));
		
	}
	
}
