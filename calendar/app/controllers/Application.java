package controllers;

import play.data.validation.Required;
import play.mvc.Controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import models.*;

public class Application extends Controller {
    public static void index() {
    	User connectedUser = User.find("email", Security.connected()).first();
    	Calendars.index(connectedUser.id);
    }
    
    public static void register() {
		render("/Users/register.html");
	}
	
	public static void newUser(String email, String password, String fullname) {
		User user = new User(email, password, fullname);
		if(user.validateAndSave()) {
			Security.authenticate(email, password);
			Calendars.index(user.id);
		} else {
			params.flash();
	        validation.keep();
	        register();
		}
	}
}