package controllers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import play.data.validation.Check;
import play.data.validation.Match;
import play.data.validation.Required;
import play.data.validation.Validation;
import play.mvc.Controller;
import models.*;

public class Events extends Controller {
	private static SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyyHH:mm");
	
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
    	try {
			event.startDate = format.parse(startDate+startTime);
			event.endDate = format.parse(endDate+endTime);
		} catch (ParseException e) {
			e.printStackTrace();
			params.flash();
        	validation.keep();
        	Events.edit(eventId);
		}
    	event.isPrivate = isPrivate;
    	event.description = description;

    	if(event.validateAndSave()) {
    		Calendars.show(event.calendar.id);
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
    	try {
			event.startDate = format.parse(startDate+startTime);
			event.endDate = format.parse(endDate+endTime);
		} catch (ParseException e) {
			e.printStackTrace(System.out);
			params.flash();
        	validation.keep();
        	Events.add(calendarId);
		}
    	
    	event.isPrivate = isPrivate;
    	event.description = description;
    	
        if (event.validateAndSave())
            Calendars.show(calendarId);
        else {
        	params.flash();
        	validation.keep();
        	Events.add(calendarId);
        }
    }
}