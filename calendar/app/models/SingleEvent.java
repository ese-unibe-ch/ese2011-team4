package models;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
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
@DiscriminatorValue("SINGLE")
public class SingleEvent extends Event {
	public SingleEvent(Calendar calendar, String name, DateTime startDate, DateTime endDate) {
		super(calendar, name, startDate, endDate);
	}
	
	public SingleEvent(EventSeries event, DateTime day) {
		super(event.origin, event.name, day, day.plusHours(1));
	}

	/**
	 *
	 * Returns true if the event takes place at a certain day
	 * 
	 * @param	day 	The day to test
	 * @return	true if the start date of the event is at this day	
	 * @see		models.SingleEvent#isThisDayandLocation(DateTime day, Location loc)
	 * @since	Beta-v1.2
	 */
	
	@Override
	public boolean isThisDay(DateTime day) {
		return startDate.getYear() == day.getYear() 
			&& startDate.getDayOfYear() == day.getDayOfYear();
	}
	
	@Override
	public boolean isThisDayandLocation(DateTime day, Location loc) {
		return startDate.getYear() == day.getYear() 
			&& startDate.getDayOfYear() == day.getDayOfYear()
			&& location.toString().contains(loc.toString());
	}
}