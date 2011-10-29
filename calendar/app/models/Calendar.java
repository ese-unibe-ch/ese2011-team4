package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Query;

import org.joda.time.DateTime;
import org.joda.time.Days;

import controllers.Events;

import play.db.jpa.JPA;
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
	
	/**
	 *
	 * Returns a list of Events for a specific day and user
	 * 
	 * @param	visitor		The user which likes to see the events 
	 * @param	day			The day requested		 
	 * @return	List<SingleEvent> List of events for the requested day and user
	 * @see		models.Calendar#events(User visitor, DateTime day, Location location)
	 * @since	Iteration-3
	 */
	
	public List<SingleEvent> events(User visitor, DateTime day) {
		DateTime start = day.withTime(0, 0, 0, 0);
		DateTime end = start.plusDays(1);
		
		// Get single events
		Query query = JPA.em().createQuery("SELECT e FROM SingleEvent e " +
				"WHERE ?1 MEMBER OF e.calendars "+
				"AND (e.isPrivate = false OR e.origin.owner = ?2) " +
				"AND e.endDate >= ?3 " +
				"AND e.startDate < ?4");
		query.setParameter(1, this);
		query.setParameter(2, visitor);
		query.setParameter(3, start);
		query.setParameter(4, end);
		
		List<SingleEvent> list = query.getResultList();
		
		query = JPA.em().createQuery("SELECT e FROM EventSeries e " +
				"WHERE ?1 MEMBER OF e.calendars " +
				"AND (e.isPrivate = false OR e.origin.owner = ?2)");
		query.setParameter(1, this);
		query.setParameter(2, visitor);
		
		for(EventSeries e : (List<EventSeries>) query.getResultList()) {
			if(e.isThisDay(day))
				list.add(e.createDummyEvent(day));
		}
		
		return list;
	}
	
	public List<SingleEvent> events(User visitor, DateTime day, Location location) {
		List<SingleEvent> list = events(visitor, day);
		List<SingleEvent> copy = new LinkedList<SingleEvent>();
		for(SingleEvent e : list)
			if(e.location.equals(location))
				copy.add(e);
		return copy;
	}
	
	public List<SingleEvent> eventsByLocation(User visitor,Location loc) {
		Query query = JPA.em().createQuery("SELECT e FROM Event e "+
				"WHERE (e.isPrivate = false OR e.creator = ?1)" +
				"AND e.location.getLocation().contains(loc.getLocation())");
		query.setParameter(1, visitor);
		return query.getResultList();
	}
	
	@Override
	public Calendar delete() {
		// First delete all events that were initially created in this calendar
		for(Event e : events)
			if(e.origin.equals(this))
				e.delete();
		return super.delete();
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