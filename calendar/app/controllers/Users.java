package controllers;

import java.util.List;

import javax.persistence.Query;

import org.joda.time.DateTime;

import models.*;
import play.db.jpa.JPA;
import play.Logger;
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
	
	public static void showUserProfile(Long id){
		User connectedUser = User.findById(id);
		UserProfile conUserProfile = UserProfile.findById(connectedUser.id);
		render(connectedUser,conUserProfile);
		
	}
}
