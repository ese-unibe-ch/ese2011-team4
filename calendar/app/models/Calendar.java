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
import javax.persistence.Query;

import org.joda.time.DateTime;
import org.joda.time.Days;

import controllers.Events;

import play.db.jpa.JPA;
import play.data.validation.Required;
import play.db.jpa.Model;


/**
 * The Calendar class represents a system of organizing days for social,
 * religious, commercial, or administrative purposes.
 * <p>
 * Calendars have a name, an owner and zero or more {@link Event}s. Owner is the {@link User}
 * who possesses the calendar. Furthermore the owner can add or remove
 * events to the calendar.
 * <p>
 * The class Calendar includes methods for:
 * <ul>
 * <li>deleting events
 * <li>showing events to a user at a specific day or a specific {@link Location} or for both
 * <li>showing all events a user is allowed to see
 * <li>representing all days in a specific month
 * </ul>
 * 
 * @since Iteration-1
 * @see Event
 * @see Location
 * @see User
 */
@Entity
public class Calendar extends Model {
	
	/**
	 * This calendar's name.
	 * 
	 * @see models.Calendar#Calendar(User, String)
	 */
	@Required
	public String name;
	
	
	/**
	 * User who owns this calendar.
	 * 
	 * @see models.Calendar#Calendar(User, String)
	 */
	@ManyToOne
	@Required
	public User owner;
	
	
	/**
	 * List of events which this calendar contains.
	 * 
	 * @see models.Calendar#Calendar(User, String)
	 */
	@ManyToMany(mappedBy="calendars")
	public List<Event> events;
	
	
	/** 
	 * Constructor of class Calendar. The default behaviour is:
	 * <ul> 
	 * <li>Calendar has a name</li> 
	 * <li>Calendar has an owner</li>
	 * <li>Calendar contains zero or more events</li> 
	 * </ul> 
	 * 
	 * @param owner		user who possesses this calendar
	 * @param name		this calendar's name
	 */
	public Calendar(User owner, String name) {
		this.owner = owner;
		this.name = name;
		this.events = new LinkedList<Event>();
	}
	
	
	/**
	 * 
	 */
	@Override
	public Calendar delete() {
		// First delete all events that were initially created in this calendar
		for(Event e : events)
			if(e.origin.equals(this))
				e.delete();
		
		return super.delete();
	}
	
	public List<Event> eventsByDay(DateTime day, User visitor) {
		DateTime start = day.withTime(0, 0, 0, 0);
		DateTime end = start.plusDays(1);
		
		Query query = JPA.em().createQuery("SELECT e FROM Event e "+
				"WHERE ?1 MEMBER OF e.calendars "+
				"AND (e.isPrivate = false OR e.origin.owner = ?2) " +
				"AND e.endDate >= ?3 " +
				"AND e.startDate < ?4");
		query.setParameter(1, this);
		query.setParameter(2, visitor);
		query.setParameter(3, start);
		query.setParameter(4, end);
		return query.getResultList();
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
	public List<Event> eventsByDayandLocation(DateTime day, User visitor,Location loc) {
		DateTime start = day.withTime(0, 0, 0, 0);
		DateTime end = start.plusDays(1);
		Query query = JPA.em().createQuery("SELECT e FROM Event e "+
				"WHERE (e.isPrivate = false OR e.creator = ?1)" +
				"AND e.startDate > ?2 " +
				"AND e.endDate < ?3" +
				"AND e.location.getLocation().contains(loc.getLocation())");
		query.setParameter(1, visitor);
		query.setParameter(2, start);
		query.setParameter(3, end);
		return query.getResultList();
	}
	public List<Event> eventsByLocation(User visitor,Location loc) {
		
		Query query = JPA.em().createQuery("SELECT e FROM Event e "+
				"WHERE (e.isPrivate = false OR e.creator = ?1)" +
				"AND e.location.getLocation().contains(loc.getLocation())");
		query.setParameter(1, visitor);
		return query.getResultList();
	}
}