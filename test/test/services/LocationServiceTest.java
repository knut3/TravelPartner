package test.services;

import static org.fest.assertions.Assertions.assertThat;
import models.view.Coordinate;

import org.junit.Test;

import play.libs.F.Promise;
import services.LocationService;
import services.interfaces.ILocationService;
import config.BaseTest;

public class LocationServiceTest extends BaseTest{

	
	private ILocationService locationService = new LocationService();

	@Test
	public void cityByCoords(){
		Coordinate oslo = new Coordinate(59.916667f, 10.75f);
		Promise<String> cityPromise = locationService.getCityByCoordinates(oslo.latitude, oslo.longitude);
		String city = cityPromise.get(10000);
		assertThat(city).isEqualToIgnoringCase("Oslo");
	}
}
