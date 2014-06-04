package models;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.db.ebean.Model;

@Entity
public class Message extends Model{

	private static final long serialVersionUID = 2L;
	@Id
	public long id;
	public long senderId;
	public long recipientId;
	public String message;
	
	
	
	public Message(long senderId, long recipientId, String message) {
		this.senderId = senderId;
		this.recipientId = recipientId;
		this.message = message;
	}



	public static Finder<Long,Message> find = new Finder<Long,Message>(
		    Long.class, Message.class
	); 
}
