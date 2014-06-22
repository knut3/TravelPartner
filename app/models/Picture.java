package models;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.db.ebean.Model;

@Entity
public class Picture extends Model{
	
	private static final long serialVersionUID = 2L;

	@Id
	public String id;
	
	public Long ownerId;
			
	public int width;
	
	public int height;
	
	public Picture(String id, Long ownerId, int width, int height) {
		this.id = id;
		this.ownerId = ownerId;
		this.width = width;
		this.height = height;
	}
	
	public static Finder<String,Picture> find = new Finder<String,Picture>(
			String.class, Picture.class
	); 

}
