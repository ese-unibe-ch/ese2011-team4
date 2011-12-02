package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class Message extends Model {
	@ManyToOne
	public MessageBox inbox;
	
	@ManyToOne
	public User sender;
	
	@Required
	@ManyToOne
	public User recipient;
	
	@Type(type="org.joda.time.contrib.hibernate.PersistentDateTime")
	public DateTime sendDate;
	
	@Required
	public String subject;
	public String content;
	public boolean read;
	
	public Message(User recipient) {
		this.recipient = recipient;
		this.read = false;
	}
	
	public void send(User sender) throws Exception {
		if(recipient == null) {
			throw new Exception();
		} else {
			sendDate = new DateTime();
			this.sender = sender;
			recipient.messageBox.getMessage(this);
		}
	}
	
	public void saveAsDraft(MessageBox messageBox) {
		messageBox.addToDrafts(this);
	}
	
	public void read() {
		assert inbox != null;
		
		if(read == false) {
			read = true;
			inbox.unreadMessages--;
			inbox.save();
			this.save();
		}
	}
}
