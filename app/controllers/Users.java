package controllers;

import java.util.List;

import models.User;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import utils.ContextArgsKeys;
import annotations.Authorization.Authorized;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;

@Authorized
public class Users extends Controller {
	
	
	public static Result getUser(Long id){
		
		User user = User.find.byId(id);
		if(user == null)
			return badRequest("Invalid ID");
		
		return ok(Json.toJson(user));
		
	}
	
	public static Result getUsersNearYou(){
		
		final float radius = 0.05f; // 3 km
		
		Long userId = Long.parseLong((String)Http.Context.current().args.get(ContextArgsKeys.USER_ID));
		User user = User.find.byId(userId);
		if(user.longitude == null || user.latitude == null)
			return status(METHOD_NOT_ALLOWED, 
					"You have to update your GPS coordinates in order to see other travelers");
		
		List<User> users = User.find
		.fetch("profilePicture")
		.where()
		 .ge("latitude", user.latitude - radius)
		 .le("latitude", user.latitude + radius)
		 .ge("longitude", user.longitude - radius)
		 .le("longitude", user.longitude + radius)
		 .not(Expr.eq("id", userId))
        .findList();
		
		return ok(Json.toJson(users));
		
	}
	
}
