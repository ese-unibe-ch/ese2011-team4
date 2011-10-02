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
        render(calendars, users);
    }
    
    public static void show(Long id) {
    	Calendar calendar = Calendar.findById(id);
    	List<Event> events = Event.find("byCalendar", calendar).fetch();
    	render(calendar, events);
    }
}