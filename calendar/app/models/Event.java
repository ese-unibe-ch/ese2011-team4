package models;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class Event extends Model implements Comparable<Event> {
	@Required
	public String name;
	
	@Required
	public Date startDate;
	
	@Required
	public Date endDate;
	
	@Lob
	public String description;
	
	public Boolean isPrivate;
	
	@ManyToOne
	public Calendar calendar;
	
	public Event(Calendar calendar, String name, Date startDate, Date endDate, boolean isPrivate) throws InvalidEventException {
		if(startDate.after(endDate))
			throw new InvalidEventException("End date before start date");
		
		this.calendar = calendar;
		this.name = name;
		this.startDate = startDate;
		this.endDate = endDate;
		this.isPrivate = isPrivate;
	}

	public boolean isThisDay(Date date) {
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
}