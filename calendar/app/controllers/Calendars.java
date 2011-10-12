package controllers;

import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;

import models.Calendar;
import models.Event;
import models.User;
import play.mvc.Controller;

public class Calendars extends Controller {
    public static void index() {
    	User connectedUser = User.find("email", Security.connected()).first();
    	if(connectedUser != null)
    		Calendars.display(connectedUser.id);
    	else {
	    	List<Calendar> calendars = Calendar.all().fetch(50);
	    	render(calendars);
    	}
    }
	
	public static void display(Long userId) {
    	User user = User.findById(userId);
    	boolean isOwner = user.email.equals(Security.connected());
    	List<Calendar> calendars = Calendar.find("owner", user).fetch();
    	List<User> users = User.all().fetch();
        render(calendars, user, users, isOwner);
	}

    public static void show(Long id, Integer year, Integer month) {
    	// Get calendar
    	Calendar calendar = Calendar.findById(id);
    	assert calendar != null;
    	
    	// Get connected user
    	User connectedUser = User.find("byEmail", Security.connected()).first();
    	
    	// Check range
    	List<Event> events;
    	if(year != null && month != null)
    		events = calendar.eventsByMonth(year, month, connectedUser);
    	else {
    		DateTime dt = new DateTime();
    		events = calendar.eventsByMonth(dt.getYear(), dt.getMonthOfYear(), connectedUser);
    	}
    	
    	render(calendar, events, calendar.owner == connectedUser);
    }
    
    public static void delete(Long id) {
    	Calendar calendar = Calendar.findById(id);
    	calendar.delete();
    	Calendars.index();
    }
}
