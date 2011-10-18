package controllers;

import java.util.List;

import javax.persistence.Query;

import models.Calendar;
import models.Favorite;
import models.User;
import play.db.jpa.JPA;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class Users extends Controller {
	public static void index() {
		List<User> users = User.all().fetch();
		User connectedUser = User.find("email", Security.connected()).first();
		List<Favorite> allFavs = Favorite.find("followerId", connectedUser.id).fetch();
		List<Favorite> favs = connectedUser.favorites(allFavs);
	    render(users,favs, connectedUser);
	}
	
	public static void display(Long userId){
		User connectedUser = User.find("email", Security.connected()).first();
		User user = User.findById(userId);
		List<User> users = User.all().fetch();
		List<Calendar> calendars = Calendar.find("owner", user).fetch();
		List<Favorite> allFavs = Favorite.find("followerId", connectedUser.id).fetch();
		List<Favorite> favs = connectedUser.favorites(allFavs);
		render(users, connectedUser, favs, calendars);
	}
	
	public static void addContact(Long userId){
		List<User> users = User.all().fetch();
		User connectedUser = User.find("email", Security.connected()).first();
		User favorite = User.findById(userId);
		new Favorite(favorite.id, connectedUser.id, favorite.fullname).save();
		List<Favorite> allFavs = Favorite.find("followerId", connectedUser.id).fetch();
		List<Favorite> favs = connectedUser.favorites(allFavs);
		render(connectedUser,favs,users);
	}
	
	public static void deleteContact(Long userId){
		List<User> users = User.all().fetch();
		User connectedUser = User.find("email", Security.connected()).first();
		Favorite deleted = Favorite.find("favoriteId", userId).first();
		Favorite.delete("favoriteId", userId);
		List<Favorite> allFavs = Favorite.find("followerId", connectedUser.id).fetch();
		List<Favorite> favs = connectedUser.favorites(allFavs);
		render(connectedUser,favs,users,deleted);
	}
}
