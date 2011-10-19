package controllers;

import java.util.List;

import javax.persistence.Query;

import models.Calendar;
import models.Favorite;
import models.User;
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
	
	public static void addContact(Long userId){
		List<User> users = User.all().fetch();
		User connectedUser = User.find("email", Security.connected()).first();
		User favorite = User.findById(userId);
		if(Favorite.find("favoriteId", userId).first()==null){
			Favorite newFav = new Favorite(favorite.id, connectedUser.id, favorite.fullname).save();
			render(connectedUser, newFav, users);
		}
		else index();
	}
	
	public static void deleteContact(Long userId){
		List<User> users = User.all().fetch();
		User connectedUser = User.find("email", Security.connected()).first();
		Favorite deleted = Favorite.find("favoriteId", userId).first();
		Favorite.delete("favoriteId", userId);
		render(connectedUser, users, deleted);
	}
	
	
}
