package controllers;

import models.Location;
import models.User;
import play.libs.F.Promise;
import play.libs.Json;
import play.mvc.Result;
import services.interfaces.ILocationService;
import annotations.Authentication.RequiresAuthentication;

import com.google.inject.Inject;

@RequiresAuthentication
public class Locations extends BaseController {
	
	@Inject
	private ILocationService locationService;
	
	public Promise<Result> setLocation(float latitude, float longitude){
		User user = User.find.ref(getCurrentUserId());
		Promise<String> cityPromise = locationService.getCityByCoordinates(latitude, longitude);
		
		return cityPromise.map(city -> {
			user.setLocation(latitude, longitude, city);
			user.save();
			return ok();
		});
		
		
	}
	
	public Result getCurrentLocation(){		
		User user = User.find.ref(getCurrentUserId());
		
		return ok(Json.toJson(new Location(user.latitude, user.longitude, user.city)));
	}
	
	
}
