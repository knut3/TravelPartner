package models;

import javax.persistence.*;

import play.db.ebean.Model;

@Entity
public class User extends Model{

	private static final long serialVersionUID = 4091781878828023687L;

	@Id
	public Long id;
	
	public String firstName;
	
	public String gender;
	
	public Long profilePictureId;
	
	public Float latitude;
	
	public Float longitude;
	
	
	
	public User(Long id, String firstName, String gender, Long profilePictureId) {
		this.id = id;
		this.firstName = firstName;
		this.gender = gender;
		this.profilePictureId = profilePictureId;
		latitude = null;
		longitude = null;
	}
	
	public void setLocation(float latitude, float longitude){
		this.latitude = latitude;
		this.longitude = longitude;
	}



	public static Finder<Long,User> find = new Finder<Long,User>(
		    Long.class, User.class
	); 
}
