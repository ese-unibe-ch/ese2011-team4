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
    	Calendars.index();
    }
    
    public static void register() {
		render("/Users/register.html");
	}
	
	public static void newUser(String email, String password, String fullname) {
		User user = new User(email, password, fullname);
		if(user.validateAndSave()) {
			Security.authenticate(email, password);
			Calendars.index();
		} else {
			params.flash();
	        validation.keep();
	        register();
		}
	}
}