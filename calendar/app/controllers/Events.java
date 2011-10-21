package controllers;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import play.Logger;
import play.data.validation.Check;
import play.data.validation.Match;
import play.data.validation.Required;
import play.data.validation.Validation;
import play.mvc.Controller;
import play.mvc.With;
import models.*;

@With(Secure.class)
public class Events extends Controller {
	private static DateTimeFormatter format = DateTimeFormat.forPattern("dd.MM.yyyyHH:mm");
	
	private static void add(Long calendarId) {
		DateTime dt = new DateTime();
		add(calendarId, dt.getYear(), dt.getMonthOfYear(), dt.getDayOfMonth());
	}
	
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
    

    public static void edit(Long calendarId, Long eventId) {
    	Event event = Event.findById(eventId);
    	List<Location> locations = Location.all().fetch();
    	
    	if(Security.check(event)) {
    		Calendar calendar = Calendar.findById(calendarId);
	    	render(calendar, event, locations);
    	} else
    		forbidden("Not your event!");
    }
    
    public static void delete(Long calendarId, Long eventId) {
    	Calendar calendar = Calendar.findById(calendarId);
    	Event event = Event.findById(eventId);
    	if(Security.check(calendar)) {
    		if(Security.check(event)) {
    			event.delete();
    			Calendars.showCurrentMonth(calendarId);
    		} else {
    			assert calendar.events.contains(event);
    			
    			calendar.events.remove(event);
    			event.calendars.remove(calendar);
    			calendar.save();
    			event.save();
    			Calendars.showCurrentMonth(calendarId);
    		}
    	} else
    		forbidden("Not your calendar!");
    	
    }
    
    public static void joinCalendar(Long calendarId, Long eventId) {
    	Calendar calendar = Calendar.findById(calendarId);
    	Event event = Event.findById(eventId);
    	event.joinCalendar(calendar);
    	Calendars.show(calendarId, event.startDate.getYear(), event.startDate.getMonthOfYear(), event.startDate.getDayOfMonth());
	}
    
    public static void update(	Long calendarId,
    							Long eventId, 
								@Required String name, 
								@Required String startDate,
								@Required String startTime,
								@Required String endDate, 
								@Required String endTime,
								boolean isPrivate, 
								String description,
								Long locationId) {
    	
    	Event event = Event.findById(eventId);
    	assert event != null;
    	
    	event.name = name;
    	try {
    		event.startDate = format.parseDateTime(startDate+startTime);
    		event.endDate = format.parseDateTime(endDate+endTime);
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
    
    public static void addEvent(	Long calendarId, 
    								String name,
    								String startDate,
    								String startTime,
    								String endDate, 
    								String endTime,
    								boolean isPrivate, 
    								String description,
    								Long locationId) {
    	
    	Calendar calendar = Calendar.findById(calendarId);
    	assert calendar != null;
    	
    	try {
    		format.parseDateTime(startDate+startTime);
    		format.parseDateTime(endDate+endTime);
    	} catch(IllegalArgumentException e) {
    		validation.addError("Start.InvalidDate", "Invalid Date");
			params.flash();
        	validation.keep();
        	Events.add(calendarId);
    	}
    	
    	Event event = new Event(calendar);
    	event.name = name;
		event.startDate = format.parseDateTime(startDate+startTime);
		event.endDate = format.parseDateTime(endDate+endTime);
    	event.isPrivate = isPrivate;
    	event.description = description;
    	
    	Location location = Location.findById(locationId);
    	event.location = location;
    	
        if (event.validateAndSave())
            Calendars.show(calendarId, event.startDate.getYear(), event.startDate.getMonthOfYear(), event.startDate.getDayOfMonth());
        else {
        	for(play.data.validation.Error e : Validation.errors())
    			Logger.error(e.message());
        	params.flash();
        	validation.keep();
        	Events.add(calendarId);
        }
    }
    
    public static void showComments(Long eventId){
    	Event event = Event.findById(eventId);
    	render(event);
    }
    
    public static void addComment(Long eventId, String author, String content){
    	Event event = Event.findById(eventId);
        event.addComment(author, content);
        showComments(eventId);
    }
}