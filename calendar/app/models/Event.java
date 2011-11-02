package models;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Column;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Query;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import play.data.validation.Check;
import play.data.validation.CheckWith;
import play.data.validation.Required;
import play.db.jpa.JPA;
import play.db.jpa.Model;


/**
 * The Event class represents a temporary and scheduled happening with a defined 
 * goal or intention, which can be added to calendars.
 * <p>
 * An event includes the following informations:
 * <ul>
 * <li>Origin: The {@link Calendar} to which the event belongs</li>
 * <li>The event's name</li>
 * <li>A list of zero or more calendars in which the event occurs</li>
 * <li>A start and an end time between which the event takes place</li>
 * <li>An event can be public or private. A private event is only visible to its owner</li>
 * <li>A {@link Location} at which the event takes place</li>
 * <li>A description</li>
 * <li>A list of {@link Comment}s related to this event</li>
 * </ul>
 * <p>
 * The class Event includes methods for:
 * <ul>
 * <li>joining this event</li>
 * <li>adding comments to this event</li>
 * <li>testing if this event is available for a join</li>
 * <li>getting the visibility of this event</li>
 * <li>getting a list of participants of this event</li>
 * </ul>
 * 
 * @since Iteration-1
 * @see User
 * @see Location
 * @see Comment
 * @see Calendar
 */
@Entity
public class Event extends Model implements Comparable<Event> {
	
	
	/**
	 * Calendar to which this events initially belongs.
	 */
	@Required
	@ManyToOne
	public Calendar origin;
	
	
	/**
	 * This event's name.
	 */
	@Required
	public String name;
	
	
	/**
	 * Calendars which joined this event.
	 */
	@Required
	@ManyToMany
	public List<Calendar> calendars;
	
	
	/**
	 * Date on which this event begins.
	 */
	@Required
	@Type(type="org.joda.time.contrib.hibernate.PersistentDateTime")
	public DateTime startDate;
	
	
	/**
	 * Date on which this even ends.
	 */
	@Required
	@Type(type="org.joda.time.contrib.hibernate.PersistentDateTime")
	@CheckWith(EndAfterBeginCheck.class)
	public DateTime endDate;
	
	
	/**
	 * Visibility of this event.
	 */
	public Boolean isPrivate;
	
	
	/**
	 * Location at which this event takes place.
	 */
	@OneToOne
	public Location location;
	
	
	/**
	 * A written remark to describe this event.
	 */
	@Lob
	public String description;
	
	
	/**
	 * Comments added to this event.
	 */
	@OneToMany(mappedBy="event", cascade=CascadeType.ALL)
	public List<Comment> comments;
	
	
	/**
	 * Event's constructor. The default behaviour is:
	 * <ul>
	 * <li>Event has zero or more comments</li>
	 * <li>Event belongs to a calendar</li>
	 * <li>Event has zero or more other calendars which joined it</li>
	 * </ul>
	 * 
	 * @param 	calendar	calendar to which this event belongs
	 * @see 	Calendar
	 * @see 	Comment
	 */
	public Event(Calendar calendar) {
		this.comments = new LinkedList<Comment>();
		this.origin = calendar;
		this.calendars = new LinkedList<Calendar>();
		this.calendars.add(calendar);
	}
	
	
	/**
	 * Adds the given calendar to this event's calendar list. The calendar list
	 * represents all calendars which have joined this event.
	 * 
	 * @param 	calendar		calendar to add to the event's calendar list
	 * @see 	models.Event#calendars
	 * @since 	Iteration-2
	 */
	public void joinCalendar(Calendar calendar) {
		calendars.add(calendar);
		calendar.events.add(this);
		calendar.save();
		this.save();
	}
	
	
	/**
	 * Adds a comment to this event. The comment has an author and some content.
	 * 
	 * @param 	author		user who posts the comment
	 * @param 	content		the text contained in the comment
	 * @return 	this event
	 * @since 	Iteration-2
	 * @see 	Comment
	 */
	public Event addComment(String author, String content) {
	    Comment newComment = new Comment(author, this);
	    newComment.content = content;
	    newComment.save();
	    this.comments.add(newComment);
	    this.save();
	    return this;
	}
	
	/**
	 *
	 * Returns a list of all calendars available for joining the event given for a certain user
	 * 
	 * @param	user 	The user which requests the join 
	 * @return	List<Calendar> List of possible calendars for a join	
	 * @see		models.Event#joinCalendar(Calendar calendar)
	 * @since	Iteration-1
	 */	
	public List<Calendar> availableJoins(User user) {
		if(!isPrivate) {
			Query query = JPA.em().createQuery("SELECT c FROM Calendar c "+
					"WHERE c.owner = ?1 "+
					"AND ?2 NOT MEMBER OF c.events");
			query.setParameter(1, user);
			query.setParameter(2, this);
			return query.getResultList();
		} else
			return new LinkedList<Calendar>();
	}

	/**
	 *
	 * Returns true if the event takes place at a certain day
	 * 
	 * @param	day 	The day to test
	 * @return	<code>true</code> if the start date of the event is at this day, otherwise <code>false</code>
	 * @see		models.Event#isThisDayandLocation(DateTime day, Location loc)
	 * @since	Beta-v1.2
	 */
	public boolean isThisDay(DateTime day) {
		return startDate.getYear() == day.getYear() 
			&& startDate.getDayOfYear() == day.getDayOfYear();
	}
	
	
	/**
	 * Returns true if the given user is the owner of this event or the
	 * event is public.
	 * 
	 * @param 	visitor who wants to see this event
	 * @return <code>true</code> if visitor is owner of this event or if this event is public, otherwise <code>false</code>
	 * @since 	Iteration-1
	 * @see 	User
	 */
	public boolean isVisible(User visitor) {
		return origin.owner.equals(visitor) || !isPrivate;
	}
	
	
	/**
	 * Returns true if the given date is the start date of this event and
	 * if the given location equals the event's location.
	 * 
	 * @param 	day		date to check whether it's equal to this event's start date or not
	 * @param 	loc		location to check whether it's equal to this event's location
	 * @return <code>true</code> if day equals this event's start date and if loc equals 
	 * this event's location, otherwise <code>false</code>
	 * @since 	Iteration-2
	 * @see 	Location
	 */
	public boolean isThisDayandLocation(DateTime day, Location loc) {
		return startDate.getYear() == day.getYear() 
			&& startDate.getDayOfYear() == day.getDayOfYear()
			&& location.toString().contains(loc.toString());
	}

	
	/**
	 * Returns this event's name.
	 * 
	 * @return this event's name
	 * @since Iteration-1
	 */
	@Override
	public String toString() {
		return name;
	}

	
	/**
	 * Checks if the argument event's start date equals this event's start date.
	 * 
	 * @param e	event which's start date is compared to this event's start date
	 * @return the value 0 if the argument event's start date is equal to this
	 * event's start date; a value less than 0 if this event's start date is before 
	 * the argument event's start date; a value greater than 0 if this event's start
	 * date is after the argument event's start date.
	 * @since Iteration-1
	 */
	@Override
	public int compareTo(Event e) {
		return startDate.compareTo(e.startDate);
	}
	
	
	/**
	 *
	 * Returns a list of users participating this event
	 * 
	 * @return	a list of all users that contain this event in one of their calendars
	 * @since	Iteration-2
	 */
	public List<User> participants(){		
		List<User> parts = new LinkedList<User>();
		for(Calendar cal: calendars)
			if(!cal.owner.equals(origin.owner) && !parts.contains(cal.owner))
				parts.add(cal.owner);
		return parts;
	}
	
	
	private static class EndAfterBeginCheck extends Check {
		public boolean isSatisfied(Object event_, Object end_) {
			Event event = (Event) event_;
			DateTime end = (DateTime) end_;
			setMessage("validation.EndAfterBeginCheck");
			return event.startDate.isBefore(end);
		}
	}
}