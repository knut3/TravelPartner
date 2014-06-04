package models;

public class Location {
	public Float longitude;
	public Float latitude;
	public String city;
	
	public Location(Float latitude, Float longitude) {
		this.longitude = longitude;
		this.latitude = latitude;
		this.city = null;
	}
	
	public Location(Float latitude, Float longitude, String city) {
		this.longitude = longitude;
		this.latitude = latitude;
		this.city = city;
	}
	
	
}
