package controllers;

import java.util.List;

import org.joda.time.DateTime;

import models.Comment;
import models.Event;
import models.Location;
import models.User;
import models.UserProfile;
import play.Logger;
import play.data.validation.Required;
import play.data.validation.Validation;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class UserProfiles extends Controller {
	public static void index() {
    	List<Location> locations = Location.all().fetch();
    	User connectedUser = User.find("byEmail", Security.connected()).first();
    	UserProfile profile=UserProfile.find(connectedUser.email);
    	List<UserProfile> profiles = UserProfile.all().fetch();
    	render(connectedUser,locations,profile,profiles);
    }

    public static void add() {
    	User connectedUser = User.find("byEmail", Security.connected()).first();
    	UserProfile profile=UserProfile.find(connectedUser.email);
    	List<Location> locations = Location.all().fetch();
	    render(connectedUser,locations,profile);
    }
    
    public static void show(Long id) {
    	UserProfile profile = UserProfile.findById(id);
    	assert profile != null;
    	
    	User connectedUser = User.find("byEmail", Security.connected()).first();
    	List<UserProfile> profiles=UserProfile.findAll();
    	
    	render(connectedUser, profile,profiles);
    }

    public static void edit(Long id) {
    	UserProfile profile = UserProfile.findById(id);
	    render(profile);
    }
    
    public static void delete(Long id) {
    	UserProfile profile = UserProfile.findById(id);
    	profile.delete();
    	index();    	
    }
    
    public static void update(	Long profileId,
								@Required String email,
								@Required String name,
								@Required String gender,
								@Required DateTime birthday,
								@Required Location loc) {
    	
    	UserProfile profile = UserProfile.findById(profileId);
    	assert profile != null;
    	
    	profile.useremail = email;
    	profile.gender =gender;
    	profile.name=name;
    	profile.birthday=birthday;
    	profile.address = loc;
    	
    	if(profile.validateAndSave()) {
        	show(profileId);
    	} else {
     		for(play.data.validation.Error e : Validation.errors())
    			Logger.error(e.message());
    		params.flash();
            validation.keep();
            edit(profileId);
    	}
    }
    
	 
	
    public static void addUserProfile(String email,String name, String gender, DateTime birthday, Location loc){
    	User connectedUser = User.find("email", Security.connected()).first();
    	UserProfile existingProfile = UserProfile.find(connectedUser.email);
    	if(existingProfile == null) {
    		UserProfile prof = new UserProfile(email, name, gender, birthday, loc);
    		if (prof.validateAndSave())
    			show(prof.id);
    		else {
    			for(play.data.validation.Error e : Validation.errors())
    				Logger.error(e.message());
    			params.flash();
    			validation.keep();
    			add();
    		}
    	} else {
    		show(existingProfile.id);
    	}
    }
}
