package controllers;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import play.Logger;
import play.data.validation.Check;
import play.data.validation.Match;
import play.data.validation.Required;
import play.data.validation.Validation;
import play.mvc.Controller;
import play.mvc.With;
import models.*;

@With(Secure.class)
public class Locations extends Controller {
	
    public static void index() {
    	List<Location> locations = Location.all().fetch();
    	render(locations);
    }

    public static void add() {
	    render();
    }
    
    public static void show(Long id) {
    	Location location = Location.findById(id);
    	assert location != null;
    	
    	User connectedUser = User.find("byEmail", Security.connected()).first();
    	List<Event> events = location.getVisibleUpcomingEvents(connectedUser);
    	long numberOfEvents = location.numberOfAllUpcomingEvents();
    	
    	render(location, events, numberOfEvents);
    }

    public static void edit(Long locationId) {
    	Location location = Location.findById(locationId);
	    render(location);
    }
    
    public static void delete(Long locationId) {
    	Location location = Location.findById(locationId);
    	List<Event> events = location.getAllEvents();
    	List<User> users = User.find("byAddress", location).fetch();
    	for(Event event: events) {
    		event.location = null;
    		event.validateAndSave();
    	}
    	for(User user: users) {
    		user.address = null;
    		user.validateAndSave();
    	}
    	location.delete();
    	index();	
    }
    
    public static void update(	Long locationId,
								@Required String street,
								@Required String num,
								@Required String city,
								@Required String country,
								@Required String pincode) {
    	
    	Location location = Location.findById(locationId);
    	assert location != null;
    	
    	location.street = street;
    	location.num = num;
    	location.city = city;
    	location.country = country;
    	location.pincode = pincode;
    	
    	if(location.validateAndSave()) {
        	show(locationId);
    	} else {
     		for(play.data.validation.Error e : Validation.errors())
    			Logger.error(e.message());
    		params.flash();
            validation.keep();
            edit(locationId);
    	}
    }
    
    public static void addLocation(	String street,
    								String num,
    								String city,
    								String country,
    								String pincode) {
    	
    	Location existingLocation = Location.find(street, num, city, country, pincode);
    	if(existingLocation == null) {
    		Location location = new Location();
    		location.street = street;
    		location.num = num;
    		location.city = city;
    		location.country = country;
    		location.pincode = pincode;
  
    		if (location.validateAndSave())
    			show(location.id);
    		else {
    			for(play.data.validation.Error e : Validation.errors())
    				Logger.error(e.message());
    			params.flash();
    			validation.keep();
    			add();
    		}
    	} else {
    		show(existingLocation.id);
    	}
    }
}