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

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import play.data.validation.Check;
import play.data.validation.CheckWith;
import play.data.validation.Required;
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
		calendar.events.add(this);
	}
	
	public boolean isThisDay(DateTime day) {
		return startDate.getYear() == day.getYear() 
			&& startDate.getDayOfYear() == day.getDayOfYear();
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

	public boolean isVisible(User visitor) {
		return origin.owner.equals(visitor) || !isPrivate;
	}
}