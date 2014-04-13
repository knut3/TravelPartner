package controllers;

import models.User;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import utils.ContextArgsKeys;
import annotations.Authorization.Authorized;

@Authorized
public class Location extends Controller {
	
	
	public static Result setLocation(Long userId, float latitude, float longitude){
		
		String authenticatedUserId = (String) Http.Context.current().args.get(ContextArgsKeys.USER_ID);
		if(!authenticatedUserId.equals(userId.toString()))
			return unauthorized("You do not have the authorization to update the location of this user");
		
		User user = User.find.byId(userId);
		user.setLocation(latitude, longitude);
		user.save();
		
		return ok();
	}
	
	
}
