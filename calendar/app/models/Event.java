package models;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Column;
import javax.persistence.OneToMany;
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
	
	@Lob
	public String description;
	
	public Event(Calendar calendar) {
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
	

/**
 *
 * Returns a list of all calendars available for joining the event given for a certain user
 * 
 * @param	User 	The user which requests the join 
 * @return	List<Calendar> List of possible calendars for a join	
 * @see		models.Event#joinCalendar(Calendar calendar)
 * @since	Beta-v1.2
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
	
	public boolean isThisDay(DateTime day) {
		return startDate.getYear() == day.getYear() 
			&& startDate.getDayOfYear() == day.getDayOfYear();
	}
	
	public boolean isVisible(User visitor) {
		return origin.owner.equals(visitor) || !isPrivate;
	}
	
	@Override
	public String toString() {
		return name;
	}

	@Override
	public int compareTo(Event e) {
		return startDate.compareTo(e.startDate);
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