package controllers;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Days;

import models.Calendar;
import models.SingleEvent;
import models.User;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class Calendars extends Controller {
	
    public static void index(Long userId) {
    	User connectedUser = User.find("email", Security.connected()).first();
    	User user = User.findById(userId);
    	List<Calendar> calendars = Calendar.find("owner", user).fetch();
        render(calendars, connectedUser, user);
    }
    
    public static void add() {
    	User connectedUser = User.find("email", Security.connected()).first();
	    render(connectedUser);
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
    	
    	List<SingleEvent> events = calendar.events(connectedUser, dt);
    	User calendarOwner = calendar.owner;
    	
    	render(calendar, dt, events, connectedUser, calendarOwner);
    }
    
    public static void delete(Long id) {
    	Calendar calendar = Calendar.findById(id);
    	if(Security.check(calendar)) {
	    	calendar.delete();
	    	Calendars.index(calendar.owner.id);
    	} else
    		forbidden("Not your calendar!");
    }
    
    public static void addCalendar(String name) {
    	User connectedUser = User.find("email", Security.connected()).first();
    	
    	Calendar calendar = new Calendar(connectedUser, name);
    	if (calendar.validateAndSave())
    		Calendars.index(connectedUser.id);
    	else {
    		params.flash();
    		validation.keep();
    		Calendars.add();
    	}
    }
}
