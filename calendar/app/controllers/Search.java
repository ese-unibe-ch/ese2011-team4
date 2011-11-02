package controllers;

import java.util.List;
import java.util.LinkedList;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import play.mvc.Controller;
import play.mvc.Http.Request;
import play.mvc.With;

import models.*;


@With(Secure.class)
public class Search extends Controller{
	
	public static void index(){
		User connectedUser = User.find("email", Security.connected()).first();
		render(connectedUser);
	}
	
	public static void search(String name, boolean searchUsers, boolean searchCalendars, boolean searchEvents){
		if(!name.equals("")){
			List<User> matchUsers = new LinkedList<User>();
			List<Calendar> matchCalendars = new LinkedList<Calendar>();
			List<Event> matchEvents = new LinkedList<Event>();
			if(searchUsers)
				matchUsers = searchUser(name);
			if(searchCalendars)
				matchCalendars = searchCalendar(name);
			if(searchEvents)
				matchEvents = searchEvent(name);
			render(matchUsers,matchEvents,matchCalendars, name, searchUsers,searchCalendars,searchEvents);
		}
		else 
			index();
	}
	
	private static List<User> searchUser(String name){
		List<User> match = new LinkedList<User>();
		List<User> users = User.all().fetch();
		for(User user: users){
			if(user.fullname.toLowerCase().contains(name.toLowerCase()) || user.email.toLowerCase().contains(name.toLowerCase()))
				match.add(user);
		}
		return match;
	}
	
	private static List<Calendar> searchCalendar(String name){
		List<Calendar> match = new LinkedList<Calendar>();
		List<Calendar> calendars = Calendar.all().fetch();
		for(Calendar cal: calendars){
			if(cal.name.toLowerCase().contains(name.toLowerCase()))
				match.add(cal);
		}
		return match;
	}
	
	private static List<Event> searchEvent(String name){
		List<Event> match = new LinkedList<Event>();
		List<Event> events = Event.all().fetch();
		for(Event event: events){
			if(event.name.toLowerCase().contains(name.toLowerCase()))
				match.add(event);
		}
		return match;
	}
}
