package controllers;

import java.util.List;

import models.User;
import models.view.UserViewModel;
import play.libs.F.Promise;
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
	
	public Promise<Result> getUsersNearYou() throws NoLocationException{
		
		Promise<List<UserViewModel>> usersPromise = userService.getUsersNearby(getCurrentUserId(), getCurrentAccessToken(), getCurrentAppSecretProof());
		
		return usersPromise.map(users -> ok(Json.toJson(users)));
				
	}
	
}
