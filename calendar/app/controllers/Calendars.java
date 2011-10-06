package controllers;

import java.util.List;

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

    public static void show(Long id) {
    	List<Event> events;
    	Calendar calendar = Calendar.findById(id);
    	boolean isOwner = calendar.owner.email.equals(Security.connected());
    	if(isOwner)
    		events = Event.find("SELECT x FROM Event x WHERE x.calendar.id = ? " +
    				"order by x.startDate asc, x.endDate asc", id).fetch();
    	else
    		events = Event.find("SELECT x FROM Event x WHERE x.calendar.id = ? " +
    				"AND x.isPrivate != true order by x.startDate asc, x.endDate asc", id).fetch();
    	
    	render(calendar, events, isOwner);
    }
    
    public static void delete(Long id) {
    	Calendar calendar = Calendar.findById(id);
    	calendar.delete();
    	Calendars.index();
    }
}
