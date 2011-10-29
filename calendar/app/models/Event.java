package models;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Query;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;


import play.data.validation.Check;
import play.data.validation.CheckWith;
import play.data.validation.Required;
import play.db.jpa.JPA;
import play.db.jpa.Model;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="EVENTTYPE", discriminatorType=DiscriminatorType.STRING)
@Table(name="Event")
public abstract class Event extends Model {
	@Required
	@ManyToOne
	public Calendar origin;
	
	@Required
	public String name;
	
	@Required
	@Type(type="org.joda.time.contrib.hibernate.PersistentDateTime")
	public DateTime startDate;
	
	@Required
	@Type(type="org.joda.time.contrib.hibernate.PersistentDateTime")
	@CheckWith(EndAfterBeginCheck.class)
	public DateTime endDate;
	
	@Required
	public RepeatingType type;
	
	@ManyToMany
	public List<Calendar> calendars;
	
	public Boolean isPrivate;
	
	@ManyToOne
	public Location location;
	
	@Lob
	public String description;
	
	@OneToMany(mappedBy="event", cascade=CascadeType.ALL)
	public List<Comment> comments;
	
	public Event(Calendar calendar, String name, DateTime startDate, DateTime endDate) {
		this.comments = new LinkedList<Comment>();
		this.origin = calendar;
		this.startDate = startDate;
		this.endDate = endDate;
		this.name = name;
		this.calendars = new LinkedList<Calendar>();
		this.calendars.add(calendar);
		this.isPrivate = false;
		this.type = RepeatingType.NONE;
	}
	
	public boolean isVisible(User visitor) {
		return origin.owner.equals(visitor) || !isPrivate;
	}
	
	public void joinCalendar(Calendar calendar) {
		calendars.add(calendar);
		calendar.events.add(this);
		calendar.save();
		this.save();
	}
	
	public void addComment(String author, String content) {
	    Comment newComment = new Comment(author, this);
	    newComment.content = content;
	    newComment.save();
	    this.comments.add(newComment);
	    this.save();
	}
	
	/**
	 *
	 * Returns a list of all calendars available for joining the event given for a certain user
	 * 
	 * @param	User 	The user which requests the join 
	 * @return	List<Calendar> List of possible calendars for a join	
	 * @see		models.SingleEvent#joinCalendar(Calendar calendar)
	 * @since	Iteration-1
	 */
	
	public List<Calendar> availableJoins(User user) {
		if(!isPrivate) {
			Query query = JPA.em().createQuery("SELECT c FROM Calendar c " +
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
	 * Returns a list of users participating this event
	 * 
	 * @return	a list of all users that contain this event in one of their calendars
	 * @since	Iteration-2
	 */
	
	public List<User> participants(){
		Query query = JPA.em().createQuery("SELECT c.owner FROM Calendar c " +
				"WHERE c IN ( SELECT c FROM Calendar c "+
							 "WHERE ?1 MEMBER OF c.events)");
		query.setParameter(1, this);
		return query.getResultList();
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	public abstract boolean isThisDay(DateTime day);
	
	public abstract boolean isThisDayandLocation(DateTime day, Location loc);
	
	static class EndAfterBeginCheck extends Check {
		public boolean isSatisfied(Object event_, Object end_) {
			Event event = (Event) event_;
			DateTime end = (DateTime) end_;
			setMessage("validation.EndAfterBeginCheck");
			return event.startDate.isBefore(end);
		}
	}
}