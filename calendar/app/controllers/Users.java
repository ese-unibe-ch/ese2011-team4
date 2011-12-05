package controllers;

import java.util.List;

import javax.mail.Session;
import javax.persistence.Query;

import org.joda.time.DateTime;
import org.joda.time.IllegalFieldValueException;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

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
		User connectedUser = getConnectedUser();
	    render(users, connectedUser);
	}
	
	public static void edit(Long userId) {
		User connectedUser = User.find("email", Security.connected()).first();
		User user=User.findById(userId);
		if(connectedUser.equals(user)) {
	    	List<Location> locations = Location.all().fetch();
		    render(user, locations);
		} else
    		forbidden("Not your user profile!");
    }

	public static void update(	Long userId,
								String fullname,
								String nickname,
								String gender,
								boolean visiblegender,
								Integer birthday,
								Integer birthmonth,
								Integer birthyear,
								boolean visiblebirthday,
								Long locationId,
								boolean visibleaddress,
								String telephone,
								boolean visibletelephone,
								String descriptionUser) {
		
				User user=User.findById(userId);
				assert user != null;

				Location location = Location.findById(locationId);

				user.address = location;
				
				DateTime birth = null;
				
				try {
					birth = new DateTime().withYear(birthyear).withMonthOfYear(birthmonth).withDayOfMonth(birthday);
					user.birthday = birth;
				} catch (IllegalFieldValueException e) {
					validation.addError("birthday.InvalidDate", "Invalid Birthday");
				}
				user.visibleaddress = visibleaddress;
				user.descriptionUser = descriptionUser;
				user.gender = gender;
				user.nickname = nickname;
				user.fullname = fullname;
				user.telephone = telephone;
				user.visiblebirthday = visiblebirthday;
				user.visibletelephone = visibletelephone;
				user.visiblegender = visiblegender;

				if(Validation.errors().isEmpty() && user.validateAndSave()) {
		        	show(userId);
		    	} else {
		     		for(play.data.validation.Error e : Validation.errors())
		    			Logger.error(e.message());
		    		params.flash();
		            validation.keep();
		            edit(userId);
		    	}
	}

	public static void show(Long id) {
		User connectedUser = User.find("email", Security.connected()).first();
    	User user = User.findById(id);
    	render(connectedUser, user);
    }
	
	public static void addFavorite(Long id, Long userId) {
		User connectedUser = User.findById(id);
		User favorite = User.findById(userId);
		connectedUser.addFavorite(favorite);
		connectedUser.save();
		flash.success("You added %s to your favorite contacts.", favorite);
	    index();
	}
	
	public static void removeFavorite(Long id, Long userId) {
		User connectedUser = User.findById(id);
		User favorite = User.findById(userId);
		connectedUser.removeFavorite(favorite);
		connectedUser.save();
		flash.success("You removed %s from your favorite contacts.", favorite);
		index();
	}
	
	public static void write(Long userId) {
		User sender = getConnectedUser();
		User recipient = User.findById(userId);
		
		Message message = new Message(sender, recipient);
		message.saveAsDraft(sender.messageBox);
		Messages.writeMessage(message.id);
	}
	
	protected static User getConnectedUser() {
		return User.find("email", Security.connected()).first();
	}
}