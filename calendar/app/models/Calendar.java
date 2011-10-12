package models;

import java.util.Date;
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
	
	public List<Event> eventsByMonth(Integer year, Integer month, User visitor) {
		List<Event> list = new LinkedList<Event>();
		for(Event e : events)
			if(e.isVisible(visitor) && e.isThisMonth(year, month))
				list.add(e);
		return list;
	}
	
	public int visibleEvents(User user) {
		int count = 0;
		for(Event e : events)
			if(owner == user || !e.isPrivate)
				count++;
		return count;
	}
	
	@Override
	public String toString() {
		return name;
	}
}