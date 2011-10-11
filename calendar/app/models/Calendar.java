package models;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class Calendar extends Model {
	@Required
	public String name;
	
	@ManyToOne
	@Required
	public User owner;
	
	@OneToMany(mappedBy="calendar", cascade=CascadeType.ALL)
	@Required
	public List<Event> events;
	
	public Calendar(User owner, String name) {
		this.owner = owner;
		this.name = name;
		this.events = new LinkedList<Event>();
	}
	
	public boolean isVisible(User user, Event event) {
		return events.contains(event) && !event.isPrivate || user == owner;
	}
	
	public int visibleEvents(User user) {
		int count = 0;
		for(Event e : events)
			if(owner == user || !e.isPrivate)
				count++;
		return count;
	}

	public Iterator<Event> getIteratorForUser(User user, Date date) {
		LinkedList<Event> list = new LinkedList<Event>();
		
		for(Event e : events)
			if(e.endDate.after(date) && isVisible(user, e))
				list.add(e);
		
		return list.iterator();
	}
	
	@Override
	public String toString() {
		return name;
	}
}