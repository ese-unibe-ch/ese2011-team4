package controllers;

import play.*;
import play.data.validation.Required;
import play.mvc.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import models.*;

public class Application extends Controller {
    public static void index() {
    	User connectedUser = User.find("email", Security.connected()).first();
    	List<Calendar> calendars = Calendar.find("owner", connectedUser).fetch();
    	List<User> users = User.all().fetch();
        render(calendars, connectedUser, users);
    }
    
    public static void showCalendar(Long id) {
    	List<Event> events;
    	Calendar calendar = Calendar.findById(id);
    	if(calendar.owner.email.equals(Security.connected()))
    		events = Event.find("SELECT x FROM Event x WHERE x.calendar.id = ? " +
    				"order by x.startDate asc, x.endDate asc", id).fetch();
    	else
    		events = Event.find("SELECT x FROM Event x WHERE x.calendar.id = ? " +
    				"AND x.isPrivate != true order by x.startDate asc, x.endDate asc", id).fetch();
    	
    	render(calendar, events);
    }
    
    public static void displayCalendars(Long id) {
    	User user = User.findById(id);
    	List<Calendar> calendars = Calendar.find("owner", user).fetch();
    	List<User> users = User.all().fetch();
        render(calendars, user, users);
    }
    
    public static void addEvent(Long id) {
    	Calendar calendar = Calendar.findById(id);
    	render(calendar);
    }
    
    public static void addEventToCalendar(Long calendarId, 
    	@Required String name, @Required String startDate, @Required String endDate, @Required String description) {
    	Calendar calendar = Calendar.findById(calendarId);
    	SimpleDateFormat format = new SimpleDateFormat("dd. MM. yyyy HH:mm");
    	
        if (validation.hasErrors()) {
            render("Application/addEvent.html", calendar);
        }
    	try {
			calendar.addEvent(name, format.parse(startDate), format.parse(startDate), false, description);
		} catch (InvalidEventException e) {
			render("Application/addEvent.html", calendar);
		} catch (ParseException e) {
			render("Application/addEvent.html", calendar);
		}
    	showCalendar(calendarId);	
    }
}