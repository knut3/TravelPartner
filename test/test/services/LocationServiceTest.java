package test.services;

import org.junit.Test;

import play.libs.F.Promise;
import models.Location;
import services.LocationService;
import static org.fest.assertions.Assertions.*;

public class LocationServiceTest {

	private LocationService locationService;
	public LocationServiceTest(){
		this.locationService = new LocationService();
	}
	
	@Test
	public void cityByCoords(){
		Location oslo = new Location(59.916667f, 10.75f);
		Promise<String> cityPromise = locationService.getCityByCoordinates(oslo.latitude, oslo.longitude);
		String city = cityPromise.get(10000);
		assertThat(city).isEqualToIgnoringCase("Oslo");
	}
	
}