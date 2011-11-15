package controllers;

import java.util.List;

import javax.persistence.Query;

import org.joda.time.DateTime;
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
		User connectedUser = User.find("email", Security.connected()).first();
	    render(users, connectedUser);
	}
	public static void edit(Long userId) {
		User  connectedUser = User.findById(userId);
    	List<Location> locations = Location.all().fetch();
	    	render(connectedUser, locations);
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
		
				User connectedUser=User.findById(userId);
				assert connectedUser != null;

				Location location = Location.findById(locationId);

			/*	connectedUser.setAddress(location);
				connectedUser.setBirthday(format.parseDateTime(birthday+ "."+birthmonth+"."+birthyear));
				connectedUser.setAddressVisibility(visibleaddress);
				connectedUser.setDescripton(descriptionUser);
				connectedUser.setGender("Male");
				connectedUser.setNickName(nickname);
				connectedUser.fullname=fullname;
				connectedUser.setTelephone(telephone);
				connectedUser.setBirthdayVisibility(visiblebirthday);
				connectedUser.setTelephoneVisibility(visibletelephone);
				connectedUser.setGenderVisibility(visiblegender);
				connectedUser.edit("connectedUser", params.all());
				connectedUser.address=location;
				connectedUser.birthday=format.parseDateTime(birthday+ "."+birthmonth+"."+birthyear);
				connectedUser.visibleaddress=visibleaddress;
				connectedUser.descriptionUser=descriptionUser;
				connectedUser.gender=gender;
				connectedUser.nickname=nickname;
				connectedUser.fullname=fullname;
				connectedUser.telephone=telephone;
				connectedUser.visiblebirthday=visiblebirthday;
				connectedUser.visibletelephone=visibletelephone;
				connectedUser.visiblegender=visiblegender;
				
				if(connectedUser.save().isPersistent()) {*/

				connectedUser.address = location;
				
				DateTime birth = new DateTime().withYear(birthyear).withMonthOfYear(birthmonth).withDayOfMonth(birthday);
				connectedUser.birthday = birth;
				System.out.println(connectedUser.birthday);
				connectedUser.visibleaddress = visibleaddress;
				connectedUser.descriptionUser = descriptionUser;
				connectedUser.gender = gender;
				connectedUser.nickname = nickname;
				connectedUser.fullname = fullname;
				connectedUser.telephone = telephone;
				connectedUser.visiblebirthday = visiblebirthday;
				connectedUser.visibletelephone = visibletelephone;
				connectedUser.visiblegender = visiblegender;
				if(connectedUser.validateAndSave()) {

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
    	User connectedUser = User.findById(id);
    	render(connectedUser);
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
