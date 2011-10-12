package controllers;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import play.data.validation.Check;
import play.data.validation.Match;
import play.data.validation.Required;
import play.data.validation.Validation;
import play.mvc.Controller;
import models.*;

public class Events extends Controller {
	private static DateTimeFormatter format = DateTimeFormat.forPattern("dd.MM.yyyyHH:mm");
	
    public static void add(Long calendarId) {
    	Calendar calendar = Calendar.findById(calendarId);
    	render(calendar);
    }
    
    public static void edit(Long eventId) {
    	Event event = Event.findById(eventId);
    	render(event);
    }
    
    public static void update(	Long eventId, 
								@Required String name, 
								@Required String startDate,
								@Required String startTime,
								@Required String endDate, 
								@Required String endTime,
								boolean isPrivate, 
								String description) {
    	
    	Event event = Event.findById(eventId);
    	assert event != null;
    	
    	event.name = name;
		event.startDate = format.parseDateTime(startDate+startTime);
		event.endDate = format.parseDateTime(endDate+endTime);
    	event.isPrivate = isPrivate;
    	event.description = description;

    	if(event.validateAndSave()) {
    		Calendars.show(event.calendar.id, null);
    	} else {
			params.flash();
        	validation.keep();
        	Events.edit(eventId);
    	}
    }
    
    public static void addEvent(	Long calendarId, 
    								String name,
    								String startDate,
    								String startTime,
    								String endDate, 
    								String endTime,
    								boolean isPrivate, 
    								String description) {
    	
    	Calendar calendar = Calendar.findById(calendarId);
    	assert calendar != null;
    	
    	Event event = new Event(calendar);
    	event.name = name;
		event.startDate = format.parseDateTime(startDate+startTime);
		event.endDate = format.parseDateTime(endDate+endTime);
    	event.isPrivate = isPrivate;
    	event.description = description;
    	
        if (event.validateAndSave())
            Calendars.show(calendarId, null);
        else {
        	params.flash();
        	validation.keep();
        	Events.add(calendarId);
        }
    }
}