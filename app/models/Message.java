package models;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import play.db.ebean.Model;

@Entity
public class Message extends Model{

	private static final long serialVersionUID = 2L;
	@Id
	public long id;
	@ManyToOne
	public User sender;
	@ManyToOne
	public User recipient;
	public String message;
	public Timestamp dateTimeSent;
	
	
	public Message(long senderId, long recipientId, String message) {
		this.sender = new User(senderId);
		this.recipient = new User(recipientId);
		this.message = message;
		dateTimeSent = Timestamp.valueOf(LocalDateTime.now());
	}
	
	public static Finder<Long,Message> find = new Finder<Long,Message>(
		    Long.class, Message.class
	); 

}
