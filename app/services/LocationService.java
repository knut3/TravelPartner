package services;

import com.fasterxml.jackson.databind.JsonNode;

import play.libs.F.Promise;
import play.libs.ws.*;
import services.interfaces.ILocationService;

public class LocationService implements ILocationService{

	@Override
	public String getCityByCoordinates(float latitude, float longitude) {
		WSRequestHolder locationReq = WS.url("http://nominatim.openstreetmap.org/reverse");
		locationReq.setQueryParameter("format", "json");
		locationReq.setQueryParameter("lat", String.valueOf(latitude));
		locationReq.setQueryParameter("lon", String.valueOf(longitude));
		locationReq.setQueryParameter("zoom", "5");
		Promise<WSResponse> locationPromise = locationReq.get();
		JsonNode locationJson = locationPromise.get(5000).asJson();
		String city = locationJson.get("display_name").asText();
		return city;
	}

	
	
}
