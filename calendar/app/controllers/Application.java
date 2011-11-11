package controllers;

import play.cache.Cache;
import play.data.validation.Required;
import play.libs.Codec;
import play.libs.Images;
import play.mvc.Controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import models.*;

public class Application extends Controller {
    public static void index() {
    	User connectedUser = User.find("email", Security.connected()).first();
    	if(connectedUser == null)
			try {
				Secure.login();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		else
    		Calendars.index(connectedUser.id);
    }
   
    
   
    public static void register() {
    	String randomID = Codec.UUID();
		render("/Users/register.html", randomID);
	}
	
	public static void newUser(	String email, 
								String password, 
								String fullname, 
								String code, 
								String randomID) {
		validation.equals(
		        code, Cache.get(randomID)
		    ).key("captcha").message("Invalid code. Please type it again");
		
		User user = new User(email, password, fullname);
		if(user.validateAndSave()) {
			Security.authenticate(email, password);
			Cache.delete(randomID);
			Calendars.index(user.id);
		} else {
			params.flash();
	        validation.keep();
	        register();
		}
	}
	
	public static void captcha(String id) {
	    Images.Captcha captcha = Images.captcha();
	    String code = captcha.getText("#333");
	    Cache.set(id, code, "10mn");
	    renderBinary(captcha);
	}
}