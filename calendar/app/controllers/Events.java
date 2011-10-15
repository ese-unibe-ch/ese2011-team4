package controllers;

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
	
	
    public static void add(Long id) {
    	User connectedUser = User.find("email", Security.connected()).first();
    	Calendar calendar = Calendar.findById(id);
    	if(calendar != null && calendar.owner.equals(connectedUser)) {//if(Security.check("owner"+id)) {
	    	render(calendar);
    	} else
    		forbidden("Not your calendar!");
    }
    
    public static void edit(Long id, Long eventId) {
    	User connectedUser = User.find("email", Security.connected()).first();
    	Event event = Event.findById(eventId);
    	if(event != null && event.creator.equals(connectedUser)) {//if(Security.check("owner"+id)) {
	    	Calendar calendar = Calendar.findById(id);
	    	render(calendar, event);
    	} else
    		forbidden("Not your calendar!");
    }
    
    public static void delete(Long id, Long eventId) {
    	User connectedUser = User.find("email", Security.connected()).first();
    	Event event = Event.findById(eventId);
    	if(event != null && event.creator.equals(connectedUser)) {//if(Security.check("owner"+id)) {
	    	
	    	event.delete();
	    	
	    	Calendars.show(id, event.startDate.getYear(), event.startDate.getMonthOfYear(), event.startDate.getDayOfMonth());
    	} else
    		forbidden("Not your calendar!");
    }
    
    public static void update(	Long currendCalendarId,
    							Long eventId, 
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
    	try {
    		event.startDate = format.parseDateTime(startDate+startTime);
    		event.endDate = format.parseDateTime(endDate+endTime);
    	} catch(IllegalArgumentException e) {
    		validation.addError("Start.InvalidDate", "Invalid Date");
			params.flash();
        	validation.keep();
        	Events.edit(currendCalendarId, eventId);
    	}
    	event.isPrivate = isPrivate;
    	event.description = description;

    	if(event.validateAndSave()) {
    		Calendars.show(currendCalendarId, event.startDate.getYear(), event.startDate.getMonthOfYear(), event.startDate.getDayOfMonth());
    	} else {
			params.flash();
        	validation.keep();
        	Events.edit(currendCalendarId, eventId);
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
    	
        if (event.validateAndSave())
            Calendars.show(calendarId, event.startDate.getYear(), event.startDate.getMonthOfYear(), event.startDate.getDayOfMonth());
        else {
        	params.flash();
        	validation.keep();
        	Events.add(calendarId);
        }
    }
}