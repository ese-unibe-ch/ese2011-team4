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

@Entity
public class Event extends Model implements Comparable<Event> {
	@Required
	@ManyToOne
	public Calendar origin;
	
	@Required
	public String name;
	
	@Required
	@ManyToMany
	public List<Calendar> calendars;
	
	@Required
	@Type(type="org.joda.time.contrib.hibernate.PersistentDateTime")
	public DateTime startDate;
	
	@Required
	@Type(type="org.joda.time.contrib.hibernate.PersistentDateTime")
	@CheckWith(EndAfterBeginCheck.class)
	public DateTime endDate;
	
	public Boolean isPrivate;
	
	@OneToOne
	public Location location;
	
	@Lob
	public String description;
	
	@OneToMany(mappedBy="event", cascade=CascadeType.ALL)
	public List<Comment> comments;
	
	public Event(Calendar calendar) {
		this.comments = new LinkedList<Comment>();
		this.origin = calendar;
		this.calendars = new LinkedList<Calendar>();
		this.calendars.add(calendar);
	}
	
	public void joinCalendar(Calendar calendar) {
		calendars.add(calendar);
		calendar.events.add(this);
		calendar.save();
		this.save();
	}
	
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
	 * @param	User 	The user which requests the join 
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
	 * @return	true if the start date of the event is at this day	
	 * @see		models.Event#isThisDayandLocation(DateTime day, Location loc)
	 * @since	Beta-v1.2
	 */
	
	public boolean isThisDay(DateTime day) {
		return startDate.getYear() == day.getYear() 
			&& startDate.getDayOfYear() == day.getDayOfYear();
	}
	
	public boolean isVisible(User visitor) {
		return origin.owner.equals(visitor) || !isPrivate;
	}
	
	public boolean isThisDayandLocation(DateTime day, Location loc) {
		return startDate.getYear() == day.getYear() 
			&& startDate.getDayOfYear() == day.getDayOfYear()
			&& location.toString().contains(loc.toString());
	}

	@Override
	public String toString() {
		return name;
	}

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
	
	static class EndAfterBeginCheck extends Check {
		public boolean isSatisfied(Object event_, Object end_) {
			Event event = (Event) event_;
			DateTime end = (DateTime) end_;
			setMessage("validation.EndAfterBeginCheck");
			return event.startDate.isBefore(end);
		}
	}
}