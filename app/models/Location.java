package models;

import play.db.ebean.Model;

public class Location extends Model{

	private static final long serialVersionUID = 276570408802260536L;
	
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
