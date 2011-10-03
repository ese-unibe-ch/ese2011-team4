package controllers;

import play.*;
import play.mvc.*;

import java.util.List;

import models.*;

public class Application extends Controller {
    public static void index() {
    	String connectedUser = Security.connected();
    	List<Calendar> calendars = Calendar.find("owner.email", connectedUser).fetch();
    	List<User> users = User.all().fetch();
        render(calendars, connectedUser, users);
    }
    
    public static void showCalendar(Long id) {
    	Calendar calendar = Calendar.findById(id);
    	
    	List<Event> events = Event.find("SELECT x FROM Event x WHERE x.calendar.id = ? order by x.startDate asc, x.endDate asc", id).fetch();
    	
    	render(calendar, events);
    }
    
    public static void displayCalendars(Long id) {
    	User user = User.findById(id);
    	List<Calendar> calendars = Calendar.find("owner", user).fetch();
    	List<User> users = User.all().fetch();
        render(calendars, user, users);
    }
}