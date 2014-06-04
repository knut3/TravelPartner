package controllers;

import models.Location;
import models.User;
import play.libs.Json;
import play.mvc.Result;
import services.interfaces.ILocationService;
import annotations.Authorization.Authorized;

import com.google.inject.Inject;

@Authorized
public class Locations extends BaseController {
	
	@Inject
	private ILocationService locationService;
	
	public Result setLocation(float latitude, float longitude){
		User user = User.find.ref(getCurrentUserId());
		String city = locationService.getCityByCoordinates(latitude, longitude);
		user.setLocation(latitude, longitude, city);
		user.save();		
		return ok();
	}
	
	public Result getCurrentLocation(){		
		User user = User.find.ref(getCurrentUserId());
		
		return ok(Json.toJson(new Location(user.latitude, user.longitude, user.city)));
	}
	
	
}
