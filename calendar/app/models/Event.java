package models;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Column;

import org.joda.time.DateTime;

import play.data.validation.Check;
import play.data.validation.CheckWith;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class Event extends Model implements Comparable<Event> {
	@Required
	public String name;
	
	@Required
	@Column(columnDefinition="TEXT")
	public DateTime startDate;
	
	@Required
	@Column(columnDefinition="TEXT")
	@CheckWith(EndAfterBeginCheck.class)
	public DateTime endDate;
	
	public Boolean isPrivate;
	
	@Lob
	public String description;
	
	@ManyToOne
	public Calendar calendar;
	
	public Event(Calendar calendar) {
		this.calendar = calendar;
		calendar.events.add(this);
	}
	
	protected boolean isThisDay(DateTime day) {
		return startDate.getYear() == day.getYear() 
			&& startDate.getDayOfYear() == day.getDayOfYear();
	}
	
	protected boolean isThisMonth(DateTime month) {
		return startDate.getYear() == month.getYear() 
			&& startDate.getMonthOfYear() == month.getMonthOfYear();
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
			return event.startDate.isBefore(end);
		}
	}

	public boolean isVisible(User visitor) {
		return calendar.owner == visitor || !isPrivate;
	}
}