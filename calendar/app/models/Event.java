package models;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;

public class Event implements Comparable<Event>{
	public long id;
	public static long currentId;
	public String name;
	public Date startDate;
	public Date endDate;
	public Boolean privacy;
	
	public Event(String name, Date startDate, Date endDate, boolean privacy) throws InvalidEventException {
		if(startDate.after(endDate))
			throw new InvalidEventException("End date before start date");
		this.name = name;
		this.startDate = startDate;
		this.endDate = endDate;
		this.privacy = privacy;
		this.id = currentId;
		Event.currentId++;
	}
	
	public boolean isThisDay(Date date) {
		Calendar start = Calendar.getInstance();
		start.setTime(startDate);
		
		Calendar day = Calendar.getInstance();
		day.setTime(date);
		
		return	start.get(Calendar.YEAR) == day.get(Calendar.YEAR) &&
				start.get(Calendar.DAY_OF_YEAR) == day.get(Calendar.DAY_OF_YEAR);
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	public Date getStartDate() {
		return startDate;
	}
	
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	public Date getEndDate() {
		return endDate;
	}
	
	public void setPrivacy(Boolean isPrivate) {
		this.privacy = isPrivate;
	}
	
	public Boolean isPrivate() {
		return privacy;
	}
	
	@Override
	public String toString() {
		return name;
	}

	@Override
	public int compareTo(Event e) {
		return startDate.compareTo(e.getStartDate());
	}
}