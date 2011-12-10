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
	
	@Required
	@ManyToOne
	public User sender;
	
	@Required
	@ManyToOne
	public User recipient;
	
	@Type(type="org.joda.time.contrib.hibernate.PersistentDateTime")
	public DateTime timeStamp;
	
	public String subject;
	public String content;
	public boolean read;
	
	public Message(User sender, User recipient) {
		this.sender = sender;
		this.recipient = recipient;
		this.subject = "";
		this.content = "";
		this.read = false;
	}
	
	public void send() throws Exception {
		if(recipient == null || sender == null) {
			throw new Exception(recipient == null ? "recipient missing" : "sender missing");
		} else {
			if(subject.isEmpty()) {
				subject = "<No subject>";
			}
			timeStamp = new DateTime();
			recipient.messageBox.getMessage(this);
			this.save();
		}
	}
	
	public void saveAsDraft(MessageBox messageBox) {
		if(!messageBox.drafts.contains(this)) {
			messageBox.addToDrafts(this);
			messageBox.save();
		}
		timeStamp = new DateTime();
		this.save();
	}
	
	public void read() {
		assert inbox != null;
		
		if(read == false) {
			read = true;
			inbox.save();
			this.save();
		}
	}
	
	public String getHtmlContent() {
		return content;
		// TODO doesn't work yet, don't know how it would work. Anybody?
		// return content.replace("\n", "&#10;");
	}
}