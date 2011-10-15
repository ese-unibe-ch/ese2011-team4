package controllers;

import java.util.List;

import models.Calendar;
import models.User;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class Users extends Controller {
	public static void index() {
		User connectedUser = User.find("email", Security.connected()).first();
		if(connectedUser != null){
			Users.display(connectedUser.id);
		}
		else {
	    	render();
		}
	}
	
	public static void display(Long userId){
		User connectedUser = User.find("email", Security.connected()).first();
		User user = User.findById(userId);
		List<User> users = User.all().fetch();
		List<Calendar> calendars = Calendar.find("owner", user).fetch();
		render(users, connectedUser, calendars);
	}
}
