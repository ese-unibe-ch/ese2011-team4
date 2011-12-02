package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import play.db.jpa.Model;

@Entity
public class Message extends Model {
	@ManyToOne
	public MessageBox messageBox;
	
	@ManyToOne
	public MessageBox origin;
	
	@ManyToOne
	public User sender;
	
	@ManyToOne
	public User recipient;
	
	@Type(type="org.joda.time.contrib.hibernate.PersistentDateTime")
	public DateTime sendDate;
	public String subject;
	public String content;
	public boolean read;
	
	public Message(MessageBox messageBox, String subject, String content) {
		this.messageBox = messageBox;
		this.subject = subject;
		this.content = content;
		this.sender = messageBox.owner;
		this.read = false;
	}
	
	public void send(User recipient) throws Exception {
		this.recipient = recipient;
		if(!recipient.messageBox.getMessage(this))
			throw new Exception();
		else
			sendDate = new DateTime();
	}
	
	public void saveAsDraft(MessageBox messageBox) {
		messageBox.addToDrafts(this);
	}
	
	public void read() {
		if(read == false) {
			read = true;
			messageBox.unreadMessages--;
			messageBox.save();
			this.save();
		}
	}
}
