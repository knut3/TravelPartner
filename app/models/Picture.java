package models;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import play.db.ebean.Model;

@Entity
public class Picture extends Model{
	
	private static final long serialVersionUID = 2L;

	@Id
	public UUID id;
	
	public Long ownerId;
		
	public String url;
	
	public int width;
	
	public int height;
	
	public Picture(UUID id, String url, Long ownerId, int width, int height) {
		this.id = id;
		this.url = url;
		this.ownerId = ownerId;
		this.width = width;
		this.height = height;
	}

	
	
	public static Finder<UUID,Picture> find = new Finder<UUID,Picture>(
			UUID.class, Picture.class
	); 
}
