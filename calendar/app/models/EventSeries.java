package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Months;
import org.joda.time.Weeks;
import org.joda.time.Years;

import play.data.validation.Required;
import play.db.jpa.Model;
import play.test.PlayJUnitRunner.StartPlay;

/**
 * The EventSeries class represents not limited series of events on
 * a regular basis, specified through the {@link RepeatingType}.
 * It's a concrete implementation of {@link Event}.
 * <p>
 * An EventSeries includes the following informations:
 * <ul>
 * <li>A list of mutated events, which aren't any longer part of
 * the series, because they were either deleted or edited.
 * </ul>
 * <p>
 * The class Event includes methods for:
 * <ul>
 * <li>A method to test if an event occurs on a specific date</li>
 * <li>A method to test if an event occurs on a specific date and a specific location</li>
 * <li>A method to create a repeating event within a certain time period</li>
 * <li>A method to create a single event out of this event series</li>
 * <li>A method to mutate a certain event</li>
 * <li>A method to test if a certain event is a mutated event.</li>
 * </ul>
 * 
 * @since Iteration-3
 * @see SingleEvent
 * @see Event
 * @see Location
 * @see RepeatingEvent
 */
@Entity
@DiscriminatorValue("SERIES")
public class EventSeries extends Event {
	
	/**
	 * List of mutated events. A mutated event is an event which has been removed from its series
	 * or which has been detached from its series to be transformed into a single event.
	 */
	@ElementCollection
	@Type(type="org.joda.time.contrib.hibernate.PersistentDateTime")
	private List<DateTime> mutations;
	
	/**
	 * The start date for the repetition.
	 */
	@Required
	@Type(type="org.joda.time.contrib.hibernate.PersistentDateTime")
	private DateTime periodStart;
	
	/**
	 * The end date for the repetition. If null it is an infinite series.
	 */
	@Type(type="org.joda.time.contrib.hibernate.PersistentDateTime")
	private DateTime periodEnd;
	
	/**
	 * The interval used for repetitions. Default value is 1.
	 */
	@Required
	private int interval;
	
	
	/**
	 * EventSeries's constructor. The default behavior is:
	 * <ul>
	 * <li>EventSeries belongs to a calendar</li>
	 * <li>EventSeries has a name</li>
	 * <li>EventSeries has a start and an end date</li>
	 * <li>EventSeries has a repeating type. See {@link RepeatingType}.</li>
	 * </ul>
	 * @param calendar			calendar to which this event belongs
	 * @param name				name of this event series
	 * @param startDate			start date of this event series
	 * @param endDate			end date of this event series
	 * @param repeating			type of the repeating rule
	 */
	public EventSeries(	Calendar calendar, 
						String name, 
						DateTime startDate, 
						DateTime endDate, 
						RepeatingType repeating) {
		
		super(calendar, name, startDate, endDate, repeating);
		mutations = new ArrayList<DateTime>();
		interval = 1;
		periodStart = startDate.withTime(0, 0, 0, 0);
	}

	/**
	 * Returns <code>true</code> if the argument date is after this event's period start
	 * and before this event's period end, otherwise <code>false</code>.
	 * 
	 * @param day	date to check whether it's between this event's period 
	 * start and this event's period end
	 * @return <code>true</code> if day is between this event's period start and this event's
	 * period end, otherwise <code>false</code>
	 * @since Iteration-2
	 */
	@Override
	public boolean isThisDay(DateTime day) {
		if(!periodStart.isAfter(day) && (periodEnd == null || day.isBefore(periodEnd)))
			if(!isMutated(day))
				switch(type) {
				case DAILY:
					return Days.daysBetween(periodStart, day).getDays()%interval == 0;
				case WEEKLY:
					return startDate.getDayOfWeek() == day.getDayOfWeek() && 
					Weeks.weeksBetween(periodStart, day).getWeeks()%interval == 0;
				case MONTHLY:
					return startDate.getDayOfMonth() == day.getDayOfMonth() && 
					Months.monthsBetween(periodStart, day).getMonths()%interval == 0;
				case YEARLY:
					return startDate.getMonthOfYear() == day.getMonthOfYear() && startDate.getDayOfYear() == day.getDayOfYear() && Years.yearsBetween(periodStart, day).getYears()%interval == 0;
				}
		return false;
	}

	/**
	 * Returns <code>true</code> if the argument date is after this event's period start
	 * and before this event's period end and if the argument location equals this event's
	 * location, otherwise <code>false</code>.
	 * 
	 * @param day	date to check whether it's between this event's period 
	 * 				start and this event's period end
	 * @param loc	location to check whether it's equal to this event's location	
	 * @return <code>true</code> if day is between this event's period start and this event's
	 * period end and if loc equals this event's location, otherwise <code>false</code>
	 * @since Iteration-2
	 * @see Location
	 */
	@Override
	public boolean isThisDayandLocation(DateTime day, Location loc) {
		return loc == this.location && isThisDay(day);
	}

	/**
	 * Creates and returns a dummy {@link RepeatingEvent} out of this event series, if the 
	 * argument date is between this event's period start and this event's period end.
	 * 
	 * @param day	start date of the created repeating event
	 * @return a new dummy repeating event
	 * @see models.EventSeries#isThisDay(DateTime day)
	 * @see RepeatingEvent
	 * @since Iteration-4
	 */
	public RepeatingEvent createDummyEvent(DateTime day) {
		assert isThisDay(day);
		RepeatingEvent event = new RepeatingEvent(this);
		event.startDate = this.startDate.withDayOfYear(day.getDayOfYear()).withYear(day.getYear());
		event.endDate = this.endDate.withDayOfYear(day.getDayOfYear()).withYear(day.getYear());
		return event;
	}
	
	/**
	 * Creates and returns a dummy {@link BirthdayEvent} out of this <code>EventSeries</code>,
	 * if the argument date is between this event's period start and this event's period end.
	 * 
	 * @param day	start date of the created <code>BirthdayEvent</code>
	 * @param user	<code>User</code> to whom the created <code>BirthdayEvent</code> belongs
	 * @return a new dummy <code>BirthdayEvent</code>
	 * @see BirthdayEvent
	 * @see User
	 * @since Iteration-7
	 */
	public BirthdayEvent createDummyBirthdayEvent(DateTime day, User user) {
		assert isThisDay(day);
		BirthdayEvent birthday = new BirthdayEvent(this, user);
		birthday.startDate = this.startDate.withDayOfYear(day.getDayOfYear()).withYear(day.getYear());
		birthday.endDate = birthday.startDate.plusDays(1);
		return birthday;
	}
	
	/**
	 * Detaches a repeating event from its series and transforms it to
     * a single event by creating a new single event with the argument date as start date.
     * 
	 * @param day	start date of the transformed single event
	 * @return <code>SingleEvent event</code>: The transformed single event
	 * @since Iteration-4
	 * @see SingleEvent
	 */
	public SingleEvent editSingleEvent(DateTime day) {
		SingleEvent event = new SingleEvent(this, day);
		if(event.validateAndSave())
			return event;
		else
			return null;
	}
	
	/**
	 * Adds a repeating event to the list of mutated events in order to transform it into a
	 * single event. Events in the list of mutated events are managed by their start date.
	 * 
	 * @param start start date of the event being transformed
	 * @since Iteration-4
	 */
	public void mutate(DateTime start) {
		mutations.add(start);
	}
	
	/**
	 * Returns <code>true</code> if the list of mutated events contains an event starting at the argument
	 * day, otherwise <code>false</code>.
	 * 
	 * @param day	date to check whether it is in the list of mutated events or not
	 * @return <code>true</code> if the argument day is included in the list of mutated events, otherwise false
	 * @since Iteration-4
	 */
	private boolean isMutated(DateTime day) {
		for(DateTime mutatedDate : mutations)
			if(day.getDayOfYear() == mutatedDate.getDayOfYear() && day.getYear() == mutatedDate.getYear())
				return true;
		return false;
	}
	
	/**
	 * Returns this event series's period start date.
	 * 
	 * @return <code>DateTime periodStart</code>: This event series's period start date
	 * @since Iteration-4
	 */
	public DateTime getPeriodStart() {
		return periodStart;
	}
	
	/**
	 * Sets this event series's period start date to the argument date.
	 * 
	 * @param periodStart : period start date of this event series is set to this date
	 * @since Iteration-4
	 */
	public void setPeriodStart(DateTime periodStart) {
		this.periodStart = periodStart;
	}
	
	/**
	 * Returns this event series's period end date.
	 * 
	 * @return <code>DateTime periodEnd</code>: This event series's period end date
	 * @since Iteration-4
	 */
	public DateTime getPeriodEnd() {
		return periodEnd;
	}
	
	/**
	 * Sets this event series's period end date to the argument date.
	 * 
	 * @param periodEnd : period end date of this event series is set to this date
	 * @since Iteration-4
	 */
	public void setPeriodEnd(DateTime periodEnd) {
		this.periodEnd = periodEnd;
	}
	
	/**
	 * Returns this event series's repeating interval.
	 * 
	 * @return <code>int interval</code>: Interval used for repetition
	 * @since Iteration-4
	 */
	public int getRepeatingInterval() {
		return interval;
	}
	
	/**
	 * Sets this event series's repeating interval to the argument repeating interval.
	 * 
	 * @param repeatingInterval : the new repeating interval of this event series's
	 * @since Iteration-4
	 */
	public void setRepeatingInterval(int repeatingInterval) {
		this.interval = repeatingInterval;
	}
}