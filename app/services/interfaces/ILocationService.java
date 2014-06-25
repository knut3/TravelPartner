package services.interfaces;

import play.libs.F.Promise;

public interface ILocationService {
	
	Promise<String> getCityByCoordinates(float latitude, float longitude);
	
}
