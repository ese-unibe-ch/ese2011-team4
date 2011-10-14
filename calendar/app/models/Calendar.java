package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import org.joda.time.DateTime;
import org.joda.time.Days;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class Calendar extends Model {
	@Required
	public String name;
	
	@ManyToOne
	@Required
	public User owner;
	
	@ManyToMany(mappedBy="calendars")
	public List<Event> events;
	
	public Calendar(User owner, String name) {
		this.owner = owner;
		this.name = name;
		this.events = new LinkedList<Event>();
	}
	
	@Override
	public Calendar delete() {
		// First delete all events that were initially created in this calendar
		for(Event e : events)
			if(e.creator.equals(owner))
				e.delete();
		
		return super.delete();
	}
	
	// TODO This can be done faster and more elegant with JPA Query
	public List<Event> eventsByDay(DateTime day, User visitor) {
		List<Event> list = new LinkedList<Event>();
		for(Event e : events)
			if(e.isVisible(visitor) && e.isThisDay(day))
				list.add(e);
		return list;
	}
	
	// TODO change this method to a method, that returns all future events as a list, visible for a specific user
	public int visibleEvents(User user) {
		int count = 0;
		for(Event e : events)
			if(owner == user || !e.isPrivate)
				count++;
		return count;
	}
	
    // Helper method to get a list of all days of a month
	public static List<DateTime> getDaysInMonth(DateTime dt) {
		DateTime first = dt.withDayOfMonth(1);
		DateTime last = first.plusMonths(1).minusDays(1);
		List<DateTime> days = new ArrayList<DateTime>(Days.daysBetween(first, last).getDays());
		
		for(DateTime day = first; day.isBefore(last); day = day.plusDays(1))
			days.add(day);
		days.add(last);
		
		return days;
	}
	
	@Override
	public String toString() {
		return name;
	}
}