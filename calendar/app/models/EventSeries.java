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
 * It's a concerte implementation of {@link Event}.
 * <p>
 * An EventSeries includes the following informations:
 * <ul>
 * <li>A list of mutated events, which are aren't any longer part of
 * the series, because they were either deleted or edited.
 * </ul>
 * <p>
 * The class Event includes methods for:
 * <ul>
 * <li>A method to test if a event occurs on a speciifc date.
 * </ul>
 * 
 * @since Iteration-3
 * @see SingleEvent
 * @see Event
 * @see RepeatingEvent
 */
@Entity
@DiscriminatorValue("SERIES")
public class EventSeries extends Event {
	@ElementCollection
	@Type(type="org.joda.time.contrib.hibernate.PersistentDateTime")
	private List<DateTime> mutations;
	
	@Required
	@Type(type="org.joda.time.contrib.hibernate.PersistentDateTime")
	private DateTime periodStart;
	
	/**
	 * The end date for the repetion. If null it is an infinite series.
	 */
	@Type(type="org.joda.time.contrib.hibernate.PersistentDateTime")
	private DateTime periodEnd;
	
	/**
	 * The interval used for repetitions. Default value is 1.
	 */
	@Required
	private int interval;
	
	public EventSeries(
			Calendar calendar, 
			String name, 
			DateTime startDate, 
			DateTime endDate, 
			RepeatingType repeating) {
		super(calendar, name, startDate, endDate, repeating);
		mutations = new ArrayList<DateTime>();
		interval = 1;
		periodStart = startDate.withTime(0, 0, 0, 0);
	}

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

	@Override
	public boolean isThisDayandLocation(DateTime day, Location loc) {
		return loc == this.location && isThisDay(day);
	}

	public RepeatingEvent createDummyEvent(DateTime day) {
		assert isThisDay(day);
		RepeatingEvent event = new RepeatingEvent(this);
		event.startDate = this.startDate.withDayOfYear(day.getDayOfYear()).withYear(day.getYear());
		event.endDate = this.endDate.withDayOfYear(day.getDayOfYear()).withYear(day.getYear());
		return event;
	}
	
	public SingleEvent editSingleEvent(DateTime day) {
		SingleEvent event = new SingleEvent(this, day);
		if(event.validateAndSave())
			return event;
		else
			return null;
	}
	
	public void mutate(DateTime start) {
		mutations.add(start);
	}
	
	private boolean isMutated(DateTime day) {
		for(DateTime mutatedDate : mutations)
			if(day.getDayOfYear() == mutatedDate.getDayOfYear() && day.getYear() == mutatedDate.getYear())
				return true;
		return false;
	}
	
	public DateTime getPeriodStart() {
		return periodStart;
	}
	
	public void setPeriodStart(DateTime periodStart) {
		this.periodStart = periodStart;
	}
	
	public DateTime getPeriodEnd() {
		return periodEnd;
	}
	
	public void setPeriodEnd(DateTime periodEnd) {
		this.periodEnd = periodEnd;
	}
	
	public int getRepeatingInterval() {
		return interval;
	}
	
	public void setRepeatingInterval(int repeatingInterval) {
		this.interval = repeatingInterval;
	}
}