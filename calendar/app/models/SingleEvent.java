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

/**
 * The SingleEvent class implements the abstract class {@link Event} and represents
 * a non repeating.
 * <p>
 * A single event includes following informations:
 * <ul>
 * <li>A single event has a name</li>
 * <li>A single event belongs to a calendar</li>
 * <li>A single event has a start and an end date</li>
 * <li>A single event has the {@link RepeatingType} NONE.</li>
 * </ul>
 * <p>
 * The class <code>SingleEvent</code> includes methods for:
 * <ul>
 * <li>A method to test if an event occurs on a specific date</li>
 * <li>A method to test if an event occurs on a specific date and a specific location.</li>
 * </ul>
 * 
 * @since Iteration-4
 * @see Calendar
 * @see Event
 * @see RepeatingType
 */
@Entity
@DiscriminatorValue("SINGLE")
public class SingleEvent extends Event {
	
	/**
	 * <code>SingleEvent</code>'s constructor. Default behavior is:
	 * <ul>
	 * <li>A SingleEvent belongs to a {@link Calendar}</li>
	 * <li>A SingleEvent has a name</li>
	 * <li>A SingleEvent has a start and an end date</li>
	 * <li>A SingleEvent has the {@link RepeatingType} NONE.</li>
	 * </ul>
	 * 
	 * @param calendar		the calendar to which this event belongs
	 * @param name			this event's name
	 * @param startDate		this event's start date
	 * @param endDate		this event's end date
	 * @since Iteration-4
	 * @see Calendar
	 * @see Event
	 * @see RepeatingType
	 */
	public SingleEvent(Calendar calendar, String name, DateTime startDate, DateTime endDate) {
		super(calendar, name, startDate, endDate, RepeatingType.NONE);
	}
	
	/**
	 * <code>SingleEvent</code>'s constructor to create a <code>SingleEvent</code>
	 * from an <code>EventSeries</code>. Default behavior is:
	 * <ul>
	 * <li>	A SingleEvent has a name which is defined by the argument <code>EventSeries series</code></li>
	 * <li>	A SingleEvent belongs to a {@link Calendar} which is defined by the argument <code>
	 * 		EventSeries series</code></li>
	 * <li>	A SingleEvent has a start and an end date which are defined by the argument <code>
	 * 		DateTime day</code></li>
	 * <li>	A SingleEvent has the {@link RepeatingType} NONE.</li>
	 * </ul>
	 * 
	 * @param event : The <code>EventSeries</code> from which this <code>SingleEvent</code> is
	 * 					generated 
	 * @param day	: The <code>DateTime</code> which defines this event's start and end date
	 * @since Iteration-4
	 * @see Calendar
	 * @see Event
	 * @see EventSeries
	 * @see RepeatingType
	 */
	public SingleEvent(EventSeries event, DateTime day) {
		super(event.origin, event.name, day, day.plusHours(1), RepeatingType.NONE);
	}

	/**
	 * Returns <code>true</code> if the argument date equals this event's start date, otherwise
	 * <code>false</code>.
	 * 
	 * @param day	date to check whether it's equal to this event's start date or not
	 * @return <code>true</code> if day equals this event's start date, otherwise <code>false</code>
	 * @since Iteration-4
	 */
	@Override
	public boolean isThisDay(DateTime day) {
		return startDate.getYear() == day.getYear() 
			&& startDate.getDayOfYear() == day.getDayOfYear();
	}
	
	/**
	 * Returns <code>true</code> if the argument date is the start date of this event and
	 * if the argument location equals this event's location, otherwise <code>false</code>.
	 * 
	 * @param 	day		date to check whether it's equal to this event's start date or not
	 * @param 	loc		location to check whether it's equal to this event's location
	 * @return <code>true</code> if day equals this event's start date and if loc equals 
	 * this event's location, otherwise <code>false</code>
	 * @since 	Iteration-4
	 * @see 	Location
	 */
	@Override
	public boolean isThisDayandLocation(DateTime day, Location loc) {
		return startDate.getYear() == day.getYear() 
			&& startDate.getDayOfYear() == day.getDayOfYear()
			&& location.toString().contains(loc.toString());
	}
}