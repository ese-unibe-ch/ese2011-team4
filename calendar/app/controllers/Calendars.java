package controllers;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Days;

import models.Calendar;
import models.Event;
import models.User;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
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
		User connectedUser = User.find("email", Security.connected()).first();
    	User user = User.findById(userId);
    	boolean isOwner = user.email.equals(Security.connected());
    	List<Calendar> calendars = Calendar.find("owner", user).fetch();
    	List<User> users = User.all().fetch();
        render(calendars, connectedUser, user, users, isOwner);
	}
	
	public static void showCurrentMonth(Long id) {
		DateTime dt = new DateTime();
		show(id, dt.getYear(), dt.getMonthOfYear(), dt.getDayOfMonth());
	}

    public static void show(Long id, Integer year, Integer month, Integer day) {
    	// Get calendar
    	Calendar calendar = Calendar.findById(id);
    	assert calendar != null;
    	assert year != null;
    	assert month != null;
    	assert day != null;
    	
    	// Get connected user
    	User connectedUser = User.find("byEmail", Security.connected()).first();
    	
    	DateTime dt = new DateTime().withYear(year).withMonthOfYear(month).withDayOfMonth(day);
    	
    	List<Event> events = calendar.eventsByDay(dt, connectedUser);
    	User calendarOwner = calendar.owner;
    	
    	render(calendar, dt, events, connectedUser, calendarOwner);
    }
    
    public static void delete(Long id) {
    	if(Security.check("owner"+id)) {
	    	Calendar calendar = Calendar.findById(id);
	    	calendar.delete();
	    	Calendars.index();
    	} else
    		forbidden("Not your calendar!");
    }
}
