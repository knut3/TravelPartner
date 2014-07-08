package models;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import play.db.ebean.Model;

@Entity
public class Location extends Model{

	private static final long serialVersionUID = 276570408802260536L;
	
	@Id @GeneratedValue
	public long id;
	public Float longitude;
	public Float latitude;
	public long userId;
	public Timestamp dateTime;

	
	public Location(long userId, Float latitude, Float longitude, String city) {
		this.longitude = longitude;
		this.latitude = latitude;
		this.userId = userId;
		this.dateTime = Timestamp.valueOf(LocalDateTime.now());
	}
	
	public static Finder<Long,Location> find = new Finder<Long,Location>(
		    Long.class, Location.class
	); 
	
}
