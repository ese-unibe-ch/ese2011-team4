package models;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import play.data.validation.Check;
import play.data.validation.CheckWith;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class Event extends Model implements Comparable<Event> {
	@Required
	public String name;
	
	@Required
	public Date startDate;
	
	@Required
	@CheckWith(EndAfterBeginCheck.class)
	public Date endDate;
	
	public Boolean isPrivate;
	
	@Lob
	public String description;
	
	@ManyToOne
	public Calendar calendar;
	
	public Event(Calendar calendar) {
		this.calendar = calendar;
	}

	protected boolean isThisDay(Date date) {
		java.util.Calendar start = java.util.Calendar.getInstance();
		start.setTime(startDate);
		
		java.util.Calendar day = java.util.Calendar.getInstance();
		day.setTime(date);
		
		return	start.get(java.util.Calendar.YEAR) == day.get(java.util.Calendar.YEAR) &&
				start.get(java.util.Calendar.DAY_OF_YEAR) == day.get(java.util.Calendar.DAY_OF_YEAR);
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
			Date end = (Date) end_;
			return event.startDate.before(end);
		}
	}
}