package models;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.joda.time.DateTime;

import play.db.jpa.Model;

@Entity
@DiscriminatorValue("SERIES")
public class EventSeries extends Event {
	public EventSeries(
			Calendar calendar, 
			String name, 
			DateTime startDate, 
			DateTime endDate, 
			RepeatingType type) {
		super(calendar, name, startDate, endDate);
		this.type = type;
	}

	@Override
	public boolean isThisDay(DateTime day) {
		switch(type) {
		case DAILY:
			return true;
		case WEEKLY:
			return startDate.getDayOfWeek() == day.getDayOfWeek();
		case MONTHLY:
			return startDate.getDayOfMonth() == day.getDayOfMonth();
		case YEARLY:
			return (startDate.getMonthOfYear() == day.getMonthOfYear() && startDate.getDayOfMonth() == day.getDayOfMonth());
		}
		return false;
	}

	@Override
	public boolean isThisDayandLocation(DateTime day, Location loc) {
		// TODO Auto-generated method stub
		return false;
	}

	public RepeatingEvent createDummyEvent(DateTime day) {
		RepeatingEvent event = new RepeatingEvent(this);
		event.startDate = this.startDate.withDayOfYear(day.getDayOfYear()).withYear(day.getYear());
		event.endDate = this.endDate.withDayOfYear(day.getDayOfYear()).withYear(day.getYear());
		return event;
	}
}