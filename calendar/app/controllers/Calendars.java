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
	
	public static void showCurrentMonth(Long id) {
		DateTime dt = new DateTime();
		
		show(id, dt.getYear(), dt.getMonthOfYear());
	}

    public static void show(Long id, Integer year, Integer month) {
    	// Get calendar
    	Calendar calendar = Calendar.findById(id);
    	assert calendar != null;
    	assert year != null;
    	assert month != null;
    	
    	// Get connected user
    	User connectedUser = User.find("byEmail", Security.connected()).first();
    	
    	DateTime dt = new DateTime().withYear(year).withMonthOfYear(month);
    	
    	List<Event> events = calendar.eventsByMonth(dt, connectedUser);
    	boolean isOwner = calendar.owner.equals(connectedUser);
    	
    	render(calendar, events, dt, isOwner);
    }
    
    public static void delete(Long id) {
    	Calendar calendar = Calendar.findById(id);
    	calendar.delete();
    	Calendars.index();
    }
}
