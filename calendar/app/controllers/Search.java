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
	
	public static void advanced() {
		User connectedUser = User.find("email", Security.connected()).first();
		List<Location> locations = Location.all().fetch();
		render(connectedUser, locations);
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
			if (nameMatches(name, event)) {
				match.add(event);
			}
		}
		return match;
	}

	public static void advancedSearch(String name, String description, String time, String date, String location) {	
		List<Event> match = new LinkedList<Event>();
		List<Event> events = Event.all().fetch();
		for(Event event: events){
			if(nameMatches(name, event) 
					&& descriptionMatches(description, event)
					&& dateMatches(time, date, event.startDate, event.endDate)
					&& locationMatches(location, event)) {
				match.add(event);
			}
		}
		
		render(match);
		
	}
	
	private static boolean locationMatches(String location, Event event) {
		// TODO Auto-generated method stub
		return true;
	}

	private static boolean descriptionMatches(String description, Event event) {
		if (description.isEmpty()) {
			return true;
		}
		return event.description.toLowerCase().contains(description.toLowerCase());
	}

	private static boolean nameMatches(String name, Event event) {
		if (name.isEmpty()) {
			return true;
		}
		return event.name.toLowerCase().contains(name.toLowerCase());
	}

	private static boolean dateMatches(String time, String date,
			DateTime startDate, DateTime endDate) {
		DateTime compareTime = null;
		
		if (!(date+time).isEmpty()) {
			try {
				DateTimeFormatter format;
				String dateTime;
				if (time.isEmpty()) {
					dateTime = date+"00:00";
				} else {
					dateTime = date+time;
				}
				format = DateTimeFormat.forPattern("dd.MM.yyyyHH:mm");
				compareTime = format.parseDateTime(dateTime);
			} catch(IllegalArgumentException e) {
				validation.addError("Start.InvalidDate", "Invalid Date");
				params.flash();
		    	validation.keep();
		    	Search.advanced();
			}
		}
		if (compareTime == null) {
			return true;
		}
		if (time.isEmpty()) {
			return (compareTime.compareTo(startDate)<=0 && compareTime.plusHours(24).compareTo(endDate)>=0);
		}
		return (compareTime.compareTo(startDate)>=0 && compareTime.compareTo(endDate)<=0);
	}
}
