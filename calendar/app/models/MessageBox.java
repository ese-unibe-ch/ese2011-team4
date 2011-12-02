package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.apache.log4j.Logger;

import play.db.jpa.Model;

@Entity
public class MessageBox extends Model {
	@OneToOne
	public User owner;
	
	@OneToMany(mappedBy="inbox", cascade=CascadeType.ALL)
	public List<Message> inbox;
	
	@OneToMany
	public List<Message> outbox;
	
	@OneToMany
	public List<Message> drafts;
	
	public int unreadMessages;
	
	public MessageBox(User owner) {
		this.owner = owner;
		inbox = new ArrayList();
		outbox = new ArrayList();
	}
	
	public Message sendMessage(Message msg) {
		try {
			msg.send(owner);
		} catch (Exception e) {
			msg.saveAsDraft(this);
		}
		return msg;
	}
	
	public void getMessage(Message msg) {
		assert msg.recipient == owner;
		assert msg.sender != null;
		
		inbox.add(msg);
		msg.save();
		this.save();
	}
	
	public void addToDrafts(Message message) {
		drafts.add(message);
		message.save();
		this.save();
	}
}