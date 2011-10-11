package models;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.Entity;

import play.data.validation.Email;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class User extends Model {
	@Email
	@Required
	public String email;
	
	@Required
	public String password;
	
	@Required
	public String fullname;
	public boolean isAdmin;

	public User(String email, String password, String fullname) {
		this.email = email;
		this.password = password;
		this.fullname = fullname;
	}
	
	public static User connect(String email, String password) {
		return find("byEmailAndPassword", email, password).first();
	}

	public Calendar createCalendar(String name) {
		return new Calendar(this,  name).save();
	}

	@Override
	public String toString() {
		return email;
	}
}
