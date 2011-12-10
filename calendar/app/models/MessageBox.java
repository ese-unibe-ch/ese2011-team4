package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import play.Logger;
import play.db.jpa.Model;

@Entity
public class MessageBox extends Model {
	@OneToOne
	public User owner;
	
	@OneToMany(mappedBy="inbox", cascade=CascadeType.ALL)
	public List<Message> inbox;
	
	@OneToMany
	public List<Message> drafts;
	
	public MessageBox(User owner) {
		this.owner = owner;
		inbox = new ArrayList();
		drafts = new ArrayList();
	}
	
	public int countUnread() {
		int count = 0;
		for(Message msg : inbox) {
			if(!msg.read)
				count++;
		}
		return count;
	}
	
	public void getMessage(Message msg) {
		assert msg.recipient == owner;
		assert msg.sender != null;
		
		inbox.add(msg);
		msg.inbox = this;
		msg.read = false;
		
		save();
	}
	
	public void addToDrafts(Message message) {
		drafts.add(message);
		message.save();
		this.save();
	}
}