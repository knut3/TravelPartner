package controllers;

import java.util.List;

import models.User;
import models.view.UserViewModel;
import play.libs.Json;
import play.mvc.Result;
import services.interfaces.IUserService;
import annotations.Authentication.RequiresAuthentication;

import com.google.inject.Inject;

import exceptions.NoLocationException;

@RequiresAuthentication
public class Users extends BaseController {
	
	
	private IUserService userService;
	
	@Inject
	public Users(IUserService userService) {
		this.userService = userService;
	}
	
	public Result getUser(Long id){
		
		User user = User.find.byId(id);
		if(user == null)
			return badRequest("Invalid ID");
		return ok(Json.toJson(user));
		
	}
	
	public Result getUsersNearYou() throws NoLocationException{
		
		List<UserViewModel> users = userService.getUsersNearby(getCurrentUserId());
		
		return ok(Json.toJson(users));
		
	}
	
}
