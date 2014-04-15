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
	
	@OneToOne
	public Picture profilePicture;
	
	public Float latitude;
	
	public Float longitude;
	
	
	
	public User(Long id, String firstName, String gender) {
		this.id = id;
		this.firstName = firstName;
		this.gender = gender;
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
