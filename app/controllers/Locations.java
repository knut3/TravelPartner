package controllers;

import models.Location;
import models.User;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import utils.ContextArgsKey;
import annotations.Authorization.Authorized;

@Authorized
public class Locations extends Controller {
	
	
	public static Result setLocation(float latitude, float longitude){
		
		String userId = (String) Http.Context.current().args.get(ContextArgsKey.USER_ID);
		User user = User.find.ref(Long.parseLong(userId));
		user.setLocation(latitude, longitude);
		user.save();
		
		return ok();
	}
	
	public static Result getCurrentLocation(){
		String userId = (String) Http.Context.current().args.get(ContextArgsKey.USER_ID);
		
		User user = User.find.ref(Long.parseLong(userId));
		
		
		return ok(Json.toJson(new Location(user.latitude, user.longitude)));
	}
	
	
}
