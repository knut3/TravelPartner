package controllers;

import models.Location;
import models.User;
import models.view.Coordinate;
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
			Location l = new Location(user.id, latitude, longitude, city);
			l.save();
			user.setLocation(l);
			user.save();
			return ok();
		});
		
		
	}
	
	public Result getCurrentLocation(){
		User user = User.find.ref(getCurrentUserId());
		if(user.currentLocation == null)
			return ok();
		
		return ok(Json.toJson(new Coordinate(user.currentLocation.latitude, user.currentLocation.longitude)));
	}
	
	
}
