package models;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.db.ebean.Model;

@Entity
public class Picture extends Model{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8795189112051442357L;

	@Id
	public Long id;
	
	public String url;
	
	public int width;
	
	public int height;
	
	public Picture(String url, int width, int height) {
		this.url = url;
		this.width = width;
		this.height = height;
	}

	
	
	public static Finder<Long,Picture> find = new Finder<Long,Picture>(
		    Long.class, Picture.class
	); 
}
