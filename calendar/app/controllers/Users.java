package controllers;

import java.util.List;

import javax.persistence.Query;

import org.joda.time.DateTime;

import models.*;
import play.db.jpa.JPA;
import play.Logger;
import play.data.validation.Required;
import play.data.validation.Validation;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class Users extends Controller {
	public static void index() {
		List<User> users = User.all().fetch();
		User connectedUser = User.find("email", Security.connected()).first();
	    render(users, connectedUser);
	}
	public static void edit(Long userId) {
		List<User> users = User.all().fetch();
		User  connectedUser = User.findById(userId);
    	List<Location> locations = Location.all().fetch();
	    	render(connectedUser, locations);
    }
	public static void update(Long userId,
							String fullname,
							String nickname,
							String gender,
							DateTime birthday,
							Location loc,
							String telephone) {
				User userprof=User.findById(userId);
 
				assert userprof != null;

				userprof.fullname=fullname;
				userprof.setNickName(nickname);
				userprof.setAddress(loc);
				userprof.setBirthday(birthday);
				userprof.setTelephone(telephone);
				userprof.setGender(gender);
				show(userId);
	}

	public static void show(Long id) {
    	User connectedUser = User.find("byEmail", Security.connected()).first();
    	List<User> users=User.findAll();
    	render(connectedUser,users);
    }
	public static void addFavorite(Long id, Long userId){
		User connectedUser = User.findById(id);
		User favorite = User.findById(userId);
		connectedUser.addFavorite(favorite);
		connectedUser.save();
		flash.success("You added %s to your favorite contacts.", favorite);
	    index();
	}
	
	public static void removeFavorite(Long id, Long userId){
		User connectedUser = User.findById(id);
		User favorite = User.findById(userId);
		connectedUser.removeFavorite(favorite);
		connectedUser.save();
		flash.success("You removed %s from your favorite contacts.", favorite);
		index();
	}
	
	
}
