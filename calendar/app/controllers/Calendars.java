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

    public static void show(Long id, DateTime dt) {
    	// Get calendar
    	Calendar calendar = Calendar.findById(id);
    	assert calendar != null;
    	
    	// Get connected user
    	User connectedUser = User.find("byEmail", Security.connected()).first();
    	
    	DateTime month = new DateTime();
    	if(dt != null)
    		month = dt;
    	
    	List<Event> events = calendar.eventsByMonth(month, connectedUser);
    	boolean isOwner = calendar.owner.equals(connectedUser);
    	
    	render(calendar, month, events, isOwner);
    }
    
    public static void previousMonth(Long id, DateTime currentMonth) {
    	show(id, currentMonth.minusMonths(1));
    }
    
    /*
    public static void nextMonth(Long id, Integer year, Integer month) {
    	DateTime dt = new DateTime();
    	dt.withYear(year).withMonthOfYear(month).plusMonths(1);
    	show(id, dt.getYear(), dt.getMonthOfYear());
    }
    */
    
    public static void delete(Long id) {
    	Calendar calendar = Calendar.findById(id);
    	calendar.delete();
    	Calendars.index();
    }
}
