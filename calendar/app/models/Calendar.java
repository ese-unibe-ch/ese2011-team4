package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

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
	
	@OneToMany(mappedBy="calendar", cascade=CascadeType.ALL)
	@Required
	public List<Event> events;
	
	public Calendar(User owner, String name) {
		this.owner = owner;
		this.name = name;
		this.events = new LinkedList<Event>();
	}
	
	@Deprecated
	public List<Event> eventsByMonth(DateTime month, User visitor) {
		List<Event> list = new LinkedList<Event>();
		for(Event e : events)
			if(e.isVisible(visitor) && e.isThisMonth(month))
				list.add(e);
		return list;
	}
	
	public List<Event> eventsByDay(DateTime day, User visitor) {
		List<Event> list = new LinkedList<Event>();
		for(Event e : events)
			if(e.isVisible(visitor) && e.isThisDay(day))
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
	
	public boolean hasEvents(DateTime day, User visitor) {
		for(Event e : events)
			if(e.isVisible(visitor) && e.isThisDay(day))
				return true;
		return false;
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