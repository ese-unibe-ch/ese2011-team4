package controllers;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;



import play.Logger;
import play.cache.Cache;
import play.data.validation.Check;
import play.data.validation.Match;
import play.data.validation.Required;
import play.data.validation.Validation;
import play.libs.Codec;
import play.libs.Images;
import play.mvc.Controller;
import play.mvc.With;
import models.*;

@With(Secure.class)
public class Events extends Controller {
	private static DateTimeFormatter format = DateTimeFormat.forPattern("dd.MM.yyyyHH:mm");
		
    public static void add(Long calendarId, Integer year, Integer month, Integer day) {
    	DateTime dt = new DateTime();
    	if(year != null && month != null && day != null)
    		dt = new DateTime().withYear(year).withMonthOfYear(month).withDayOfMonth(day);
    	
    	Calendar calendar = Calendar.findById(calendarId);
    	List<Location> locations = Location.all().fetch();
    	if(Security.check(calendar)) {
	    	render(calendar, dt, locations);
    	} else
    		forbidden("Not your calendar!");
    }
    
    public static void addEvent(	Long calendarId, 
									String name,
									String startDay,
									String startTime,
									String endDay, 
									String endTime,
									boolean isPrivate, 
									String description,
									Long locationId,
									RepeatingType repeating,
									String periodEndDay,
									int repeatingInterval) {
		
		Calendar calendar = Calendar.findById(calendarId);
		assert calendar != null;
		DateTime startDate = null;
		DateTime endDate = null;
		DateTime periodEnd = null;	
		try {
			startDate = format.parseDateTime(startDay+startTime);
			endDate = format.parseDateTime(endDay+endTime);
			if(!periodEndDay.isEmpty())
				periodEnd = format.parseDateTime(periodEndDay+"23:59");
		} catch(IllegalArgumentException e) {
			validation.addError("Start.InvalidDate", "Invalid Date");
			params.flash();
	    	validation.keep();
	    	DateTime dt = new DateTime();
	    	Events.add(calendarId, dt.getYear(), dt.getMonthOfYear(), dt.getDayOfMonth());
		}
		
		Event event = Event.createEvent(calendar, name, startDate, endDate, repeating, periodEnd, repeatingInterval);
		
		event.isPrivate = isPrivate;
		event.description = description;
		
		Location location = Location.findById(locationId);
		event.location = location;
		
	    if (event.validateAndSave()) {
	        Calendars.show(calendarId, event.startDate.getYear(), event.startDate.getMonthOfYear(), event.startDate.getDayOfMonth());
	    }
	    else {
	    	for(play.data.validation.Error e : Validation.errors())
				Logger.error(e.message());
	    	params.flash();
	    	validation.keep();
	    	DateTime dt = new DateTime();
	    	Events.add(calendarId, dt.getYear(), dt.getMonthOfYear(), dt.getDayOfMonth());
	    }
	}

	public static void edit(Long calendarId, Long eventId) {
    	Event event = Event.findById(eventId);
    	List<Location> locations = Location.all().fetch();
    	
    	if(Security.check(event)) {
    		Calendar calendar = Calendar.findById(calendarId);
	    	render(calendar, event, locations);
    	} else
    		forbidden("Not your event!");
    }
    
    public static void update(	Long calendarId,
								Long eventId, 
								String name, 
								String startDay,
								String startTime,
								String endDay, 
								String endTime,
								boolean isPrivate, 
								String description,
								Long locationId,
								RepeatingType repeating,
								String periodEndDay,
								int repeatingInterval) throws Exception {
    	
		if(repeatingInterval == 0) {
			repeatingInterval = 1;
		}
    	
    	Event event = Event.findById(eventId);
    	assert event != null;
		
		if(event.type != RepeatingType.NONE && repeating == RepeatingType.NONE)
			event = Event.convertFromSeries((EventSeries) event);
		else if(event.type == RepeatingType.NONE && repeating != RepeatingType.NONE) 
			event = Event.convertFromSingleEvent((SingleEvent) event, repeating);
		
		// Update fields
		event.name = name;
		DateTime periodEnd = null;	
		try {
			event.startDate = format.parseDateTime(startDay+startTime);
			event.endDate = format.parseDateTime(endDay+endTime);
			if(!periodEndDay.isEmpty())
				periodEnd = format.parseDateTime(periodEndDay+"23:59");
			if(repeating != RepeatingType.NONE) {
				EventSeries series = (EventSeries) event;
				series.setPeriodEnd(periodEnd);
				series.type = repeating;
			}
		} catch(IllegalArgumentException e) {
			validation.addError("Start.InvalidDate", "Invalid Date");
			params.flash();
	    	validation.keep();
	    	Events.edit(calendarId, eventId);
		}
		
		event.isPrivate = isPrivate;
		event.description = description;
		
		Location location = Location.findById(locationId);
		event.location = location;
		
		if(repeating != RepeatingType.NONE) {
			EventSeries series = (EventSeries) event;
			series.setRepeatingInterval(repeatingInterval);
		}
		
		// Validate and save
		if(event.validateAndSave()) {
	    	Calendars.show(calendarId, event.startDate.getYear(), 
	    							   event.startDate.getMonthOfYear(), 
	   								   event.startDate.getDayOfMonth());
		} else {
	 		for(play.data.validation.Error e : Validation.errors())
				Logger.error(e.message());
			params.flash();
	        validation.keep();
	        Events.edit(calendarId, eventId);
		}
	}

	public static void delete(Long calendarId, Long eventId) {
    	Calendar calendar = Calendar.findById(calendarId);
    	Event event = Event.findById(eventId);
    	if(Security.check(calendar)) {
    		if(Security.check(event)) {
    			event.delete();
    			Calendars.showCurrentMonth(calendarId,false);
    		} else {
    			// Delete a joined event
    			assert calendar.events.contains(event);
    			
    			calendar.events.remove(event);
    			event.calendars.remove(calendar);
    			calendar.save();
    			event.save();
    			Calendars.show(calendarId, event.startDate.getYear(), event.startDate.getMonthOfYear(), event.startDate.getDayOfMonth());
    		}
    	} else
    		forbidden("Not your calendar!");
    }
    
    /**
     * Detaches a repeating event from its series and transforms it to
     * a single event.
     * 
     * @param calendarId	id of the calendar
     * @param eventId		id of the series
     * @param day			id of the selected day
     * @param year			id of the selected year
     */
    public static void deatachAndEdit(Long calendarId, Long eventId, int day, int year) {
    	Calendar calendar = Calendar.findById(calendarId);
    	EventSeries series = EventSeries.findById(eventId);
    	assert calendar != null && series != null;
    	DateTime dt = new DateTime().withYear(year).withDayOfYear(day);
    	
    	if(Security.check(series)) {
    		series.mutate(dt);
    		series.save();
    		edit(calendarId, series.editSingleEvent(dt).id);
    	} else
    		forbidden("Not your event!");
    }
    
    /**
     * Removes a repeating event from its series.
     * 
     * @param calendarId	id of the calendar
     * @param eventId		id of the series
     * @param day			id of the selected day
     * @param year			id of the selected year
     */
    public static void deatach(Long calendarId, Long eventId, int day, int year) {
    	Calendar calendar = Calendar.findById(calendarId);
    	EventSeries series = EventSeries.findById(eventId);
    	assert calendar != null && series != null;
    	DateTime dt = new DateTime().withYear(year).withDayOfYear(day);
    	
    	if(Security.check(calendar)) {
    		if(Security.check(series)) {
    			series.mutate(dt);
    			series.save();
    			Calendars.show(calendarId, dt.getYear(), dt.getMonthOfYear(), dt.getDayOfMonth());
    		} else
    			forbidden("Not your event!");
    	} else
    		forbidden("Not your calendar!");
    }
    
    public static void joinCalendar(Long calendarId, Long eventId) {
    	Calendar calendar = Calendar.findById(calendarId);
    	Event event = Event.findById(eventId);
    	event.joinCalendar(calendar);
    	Calendars.show(calendarId, event.startDate.getYear(), event.startDate.getMonthOfYear(), event.startDate.getDayOfMonth());
	}
    
    public static void checkLocationCollision(String startDate,
			String startTime,
			String endDate, 
			String endTime,
			Long locationId,
			long eventId) {
    	long numberOfEvents = 0;
    			
    	try {
    		format.parseDateTime(startDate+startTime);
    		format.parseDateTime(endDate+endTime);
    	} catch(IllegalArgumentException e) {
    		render(numberOfEvents);
    	}
    	    	
    	DateTime start = format.parseDateTime(startDate+startTime);
    	DateTime end = format.parseDateTime(endDate+endTime);
    	    	
    	Location location = Location.findById(locationId);
    	if(location != null) {
    		numberOfEvents = location.numberOfAllEventsByDayAndTime(start, end);
    		Event event = Event.findById(eventId);
    		if(event != null && start.isBefore(event.endDate) && end.isAfter(event.startDate) && location.equals(event.location)) {
    			numberOfEvents--;
    		}
    	}
    	render(numberOfEvents);
    }
    
    public static void checkEventCollision(String startDate,
			String startTime,
			String endDate, 
			String endTime,
			long calendarId,
			long eventId) {
    	long numberOfEvents = 0;
    			
    	try {
    		format.parseDateTime(startDate+startTime);
    		format.parseDateTime(endDate+endTime);
    	} catch(IllegalArgumentException e) {
    		render(numberOfEvents);
    	}
    	    	
    	DateTime start = format.parseDateTime(startDate+startTime);
    	DateTime end = format.parseDateTime(endDate+endTime);
    	    	
    	Calendar calendar = Calendar.findById(calendarId);
    	Event event = Event.findById(eventId);
    	if(calendar != null) {
    		numberOfEvents = calendar.numberOfAllEventsInCalendarByDayAndTime(start, end);
        	if(event != null && start.isBefore(event.endDate) && end.isAfter(event.startDate)) {
        		numberOfEvents--;
        	}
    	}
    	render(numberOfEvents);
    }
}