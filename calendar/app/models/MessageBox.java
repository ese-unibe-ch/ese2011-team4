package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import play.db.jpa.Model;

@Entity
public class MessageBox extends Model {
	@OneToOne
	public User owner;
	
	@OneToMany(mappedBy="messageBox", cascade=CascadeType.ALL)
	public List<Message> inbox;
	
	@OneToMany
	public List<Message> sent;
	
	@OneToMany
	public List<Message> drafts;
	
	public int unreadMessages;
	
	public MessageBox(User owner) {
		this.owner = owner;
		inbox = new ArrayList();
		sent = new ArrayList();
	}
	
	public Message sendMessage(String subject, String content, User recipient) {
		Message msg = new Message(this, subject, content);
		try {
			msg.send(recipient);
		} catch (Exception e) {
			msg.saveAsDraft(this);
		}
		return msg;
	}
	
	public boolean getMessage(Message msg) {
		inbox.add(msg);
		msg.save();
		this.save();
		return true;
	}
	
	public void addToDrafts(Message message) {
		drafts.add(message);
		message.save();
		this.save();
	}
}