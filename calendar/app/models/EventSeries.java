package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import play.db.jpa.Model;

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
	
	public EventSeries(
			Calendar calendar, 
			String name, 
			DateTime startDate, 
			DateTime endDate, 
			RepeatingType repeating) {
		super(calendar, name, startDate, endDate, repeating);
		mutations = new ArrayList<DateTime>();
	}

	@Override
	public boolean isThisDay(DateTime day) {
		if(!isMutated(day))
			switch(type) {
			case WEEKLY:
				return startDate.getDayOfWeek() == day.getDayOfWeek();
			case MONTHLY:
				return startDate.getDayOfMonth() == day.getDayOfMonth();
			case YEARLY:
				return startDate.getMonthOfYear() == day.getMonthOfYear() && startDate.getDayOfYear() == day.getDayOfYear();
			default:
				return true;
			}
		else
			return false;
	}

	@Override
	public boolean isThisDayandLocation(DateTime day, Location loc) {
		// TODO Auto-generated method stub
		return false;
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
}