package controllers;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import play.data.validation.Required;
import play.mvc.Controller;
import models.*;

public class Events extends Controller {
    public static void addEvent(Long calendarId) {
    	Calendar calendar = Calendar.findById(calendarId);
    	render(calendar);
    }
    
    public static void editEvent(Long eventId) {
    	Event event = Event.findById(eventId);
    	render(event);
    }
    
    public static void addEventToCalendar(	Long calendarId, 
    										@Required String name, 
    										@Required String startDate, 
    										@Required String endDate, 
    										boolean isPrivate, 
    										@Required String description) {
    	
    	Calendar calendar = Calendar.findById(calendarId);
    	
    	SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy hh:mm");
    	
        if (validation.hasErrors()) {
            params.flash();
            validation.keep();
            render("Application/addEvent.html", calendar);
        }
    	try {
			calendar.addEvent(name, format.parse(startDate), format.parse(startDate), isPrivate, description);
		} catch (InvalidEventException e) {
			e.printStackTrace(System.out);
			render("Application/addEvent.html", calendar);
		} catch (ParseException e) {
			e.printStackTrace(System.out);
			render("Application/addEvent.html", calendar);
		}
    	Calendars.show(calendarId);	
    }
}