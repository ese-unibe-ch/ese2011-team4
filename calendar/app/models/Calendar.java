package models;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.db.jpa.Model;

@Entity
public class Calendar extends Model {
	public String name;
	
	@ManyToOne
	public User owner;
	
	@OneToMany(mappedBy="calendar", cascade=CascadeType.ALL)
	public List<Event> events;
	
	public Calendar(User owner, String name) {
		this.owner = owner;
		this.name = name;
		this.events = new LinkedList<Event>();
	}
	
	public Event addEvent(String name, Date startDate, Date endDate, boolean isPrivate) throws InvalidEventException {
		Event event = new Event(this, name, startDate, endDate, isPrivate).save();
		events.add(event);
		this.save();
		return event;
	}

	public LinkedList<Event> getListForDate(User user, Date date) {
		LinkedList<Event> list = new LinkedList<Event>();
		
		for(Event e : events)
			if(e.isThisDay(date) && (isVisible(user, e)))
				list.add(e);
		
		return list;
	}
	
	public boolean isVisible(User user, Event event) {
		return events.contains(event) && !event.isPrivate || user == owner;
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