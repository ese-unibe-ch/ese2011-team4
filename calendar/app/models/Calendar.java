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

/**
 * The Calendar class represents a system of organizing days for social,
 * religious, commercial, or administrative purposes.
 * <p>
 * Calendars have a name, an owner and zero or more {@link Event}s. Owner is the {@link User}
 * who possesses the calendar.
 * <p>
 * The class Calendar includes methods for:
 * <ul>
 * <li>deleting the calendar
 * <li>showing the calendar's events at a specific day to a user 
 * <li>showing events at a specific day and a specific {@link Location} to a user
 * <li>showing events at a specific location to a user
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
	 * Calendar's constructor. The default behavior is:
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
	 * This method first deletes all events that were initially 
	 * created in this calendar. After that, it deletes this calendar itself.
	 * 
	 * @since 	Iteration-1
	 * @see 	models.Event#origin
	 */
	@Override
	public Calendar delete() {
		// First delete the event - calendar relation
		for(Event e : events) {
			if(e.origin.equals(this))
				e.delete();
			else {
				e.calendars.remove(this);
				e.save();
			}
		}
		
		return super.delete();
	}
		
	/**
	 * Returns a list of all events available in this calendar 
	 * at a specific day for a certain user.
	 * <p>
	 * The event is only visible when the given user is the owner of this event
	 * or the event itself is public.
	 * 
	 * @param 	day			the day to check for events
	 * @param 	visitor		the user who wants to see the events
	 * @return	list of available events in this calendar under the defined constrictions
	 * @see 	Event
	 * @see		User
	 * @since 	Iteration-1
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

	/**
	 * Returns a list of all events available at a specific location for a certain user.
	 * <p>
	 * An event is only visible to the user if he is the owner of that event or the event
	 * itself is public.
	 * 
	 * @param 	visitor	the user who wants to see the events
	 * @param 	loc		the location to check for events
	 * @return list of available events under the defined constraints
	 * @see		Event
	 * @see 	Location
	 * @see		User
	 * @since 	Iteration-1
	 */
	public List<Event> events(User visitor, Location loc) {
		
		Query query = JPA.em().createQuery("SELECT e FROM Event e "+
				"WHERE (e.isPrivate = false OR e.creator = ?1)" +
				"AND e.location.getLocation().contains(loc.getLocation())");
		query.setParameter(1, visitor);
		return query.getResultList();
	}

	/**
	 * Returns a list of all events available at a specific day and a specific location
	 * for a certain user.
	 * <p>
	 * An event is only visible when the given user is the owner of that event or the
	 * event itself is public.
	 * 
	 * @param 	day		the day to check for events
	 * @param 	visitor	the user who wants to see the events
	 * @param 	loc		the location to check for events
	 * @return	list of available events under the defined constraints
	 * @see		Event
	 * @see 	Location
	 * @see 	User
	 * @since 	Iteration-1
	 */
	public List<SingleEvent> events(User visitor, DateTime day, Location location) {
		List<SingleEvent> list = events(visitor, day);
		List<SingleEvent> copy = new LinkedList<SingleEvent>();
		for(SingleEvent e : list)
			if(e.location.equals(location))
				copy.add(e);
		return copy;
	}
	
	/**
	 * Returns the number of events available for a certain user.
	 * <p>
	 * The events are only available if the user is the owner of the event
	 * or the event itself is public.
	 * 
	 * @param 	user	the user for whom the method checks for available events
	 * @return 	number of available events under the defined constrictions
	 * @see		Event
	 * @see 	User
	 * @since 	Iteration-1
	 */
	// TODO change this method to a method, that returns all future events as a list, visible for a specific user
	public int visibleEvents(User user) {
		int count = 0;
		for(Event e : events)
			if(owner == user || !e.isPrivate)
				count++;
		return count;
	}
	
	/**
	 * Returns a list of all days of a certain month.
	 * 
	 * @param 	dt	the month for which to get the days
	 * @return a list of all days for this month
	 * @since 	Iteration-1
	 */
	public static List<DateTime> getDaysInMonth(DateTime dt) {
		DateTime first = dt.withDayOfMonth(1);
		DateTime last = first.plusMonths(1).minusDays(1);
		List<DateTime> days = new ArrayList<DateTime>(Days.daysBetween(first, last).getDays());
		
		for(DateTime day = first; day.isBefore(last); day = day.plusDays(1))
			days.add(day);
		days.add(last);
		
		return days;
	}
	
	/**
	 * Returns this calendar's name.
	 */
	@Override
	public String toString() {
		return name;
	}
}